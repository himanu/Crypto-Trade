package com.example.trade.controllers;

import com.example.trade.domain.JwtUtility;
import com.example.trade.domain.VERIFICATION_TYPE;
import com.example.trade.entities.OTPs;
import com.example.trade.entities.TwoFactorAuth;
import com.example.trade.entities.User;
import com.example.trade.repositories.OTPsRepository;
import com.example.trade.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    JwtUtility jwtUtility;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    OTPsRepository otPsRepository;

    @PostMapping("/login")
    ResponseEntity<Object> login(@RequestBody User user) {
        String email = user.getEmail();
        String password = user.getPassword();
        if (email == null || password == null) {
            return new ResponseEntity<>("Missing required credentials", HttpStatus.BAD_REQUEST);
        }
        User fetchedUser = userRepository.findByEmail(email);
        if (fetchedUser == null)
            return  new ResponseEntity<>("User don't exist", HttpStatus.BAD_REQUEST);

        if (passwordEncoder.matches(password, fetchedUser.getPassword())) {
            if (fetchedUser.getTwoFactorAuth().isEnabled()) {
                OTPs fetchedOTP = otPsRepository.findByUser(fetchedUser);
                if (fetchedOTP != null)
                    otPsRepository.deleteById(fetchedOTP.getId());

                OTPs savedOTP = this.createOtp();
                savedOTP.setUser(fetchedUser);
                otPsRepository.save(savedOTP);

                this.sendEmail(email, "OTP to login to your account", "Your OTP is \n" + savedOTP.getOtp());
                System.out.println("2FA Enabled");
                return new ResponseEntity<>("Sent OTP", HttpStatus.OK);
            }
            String jwtToken = jwtUtility.generateToken(email);
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtToken);
            response.put("message", "Signed in successfully!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    public OTPs createOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        // Current time
        Instant now = Instant.now();

        // Calculate expiry time (5 minutes later)
        Instant expiryTime = now.plus(Duration.ofMinutes(5));

        // Create OTP instance
        OTPs otpEntity = new OTPs();
        otpEntity.setOtp(String.valueOf(otp));
        otpEntity.setCreatedDate(Date.from(now));
        otpEntity.setExpiryDate(Date.from(expiryTime));

        return otpEntity;
    }

    @PostMapping("/signup")
    ResponseEntity<Object> signup(@RequestBody User user) throws Exception {
        String email = user.getEmail();
        String password = user.getPassword();
        String username = user.getUsername();

        if (email == null || password == null || username == null) {
            return new ResponseEntity<>("Missing required credentials", HttpStatus.BAD_REQUEST);
        }
        User isUserExist = userRepository.findByEmail(email);
        if (isUserExist != null) {
            return new ResponseEntity<>("User already exist", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setUsername(username);
        User savedUser = userRepository.save(newUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/enable_2fa")
    ResponseEntity<Object> enable2FA() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email);
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(VERIFICATION_TYPE.EMAIL);
        user.setTwoFactorAuth(twoFactorAuth);
        userRepository.save(user);
        return new ResponseEntity<>("Enabled Two Factor Auth", HttpStatus.ACCEPTED);
    }

    @PostMapping("/opt_verify")
    ResponseEntity<Object> verifyOTP(@RequestParam String otp, @RequestParam String email) {
        User fetchedUser = userRepository.findByEmail(email);
        OTPs fetchedOTP = otPsRepository.findByUser(fetchedUser);
        if (fetchedOTP == null) {
            return new ResponseEntity<>("Please login again using Email and Password", HttpStatus.UNAUTHORIZED);
        }
        if (new Date().after(fetchedOTP.getExpiryDate())) {
            return new ResponseEntity<>("OTP has expired", HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(fetchedOTP.getOtp(), otp)) {
            String jwtToken = jwtUtility.generateToken(email);
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtToken);
            response.put("message", "Signed in successfully!");
            fetchedOTP.setExpiryDate(new Date());
            otPsRepository.save(fetchedOTP);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>("Incorrect OTP", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/get_user")
    ResponseEntity<Object> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
