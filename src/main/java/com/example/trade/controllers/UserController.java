package com.example.trade.controllers;

import com.example.trade.DTOs.SetNewPasswordReq;
import com.example.trade.domain.Endpoints;
import com.example.trade.domain.JwtUtility;
import com.example.trade.domain.OTPType;
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

    @PostMapping(Endpoints.login)
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
//                Before sending a fresh otp, delete the existing one
                Optional<OTPs> fetchedOTP = otPsRepository.findByUserAndOtpType(fetchedUser, OTPType.LOGIN);
                fetchedOTP.ifPresent(otPs -> otPsRepository.deleteById(otPs.getId()));

                OTPs savedOTP = this.createOtp(fetchedUser, OTPType.LOGIN);
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

    public OTPs createOtp(User user, OTPType otpType) {
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
        otpEntity.setUser(user);
        otpEntity.setOtpType(otpType);
        return otpEntity;
    }

    @PostMapping(Endpoints.signup)
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

    @PutMapping(Endpoints.enable2FA)
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

    @PostMapping(Endpoints.otpVerify)
    ResponseEntity<Object> verifyOTP(@RequestParam String otp, @RequestParam String email) {
        User fetchedUser = userRepository.findByEmail(email);
        Optional<OTPs> fetchedOTP = otPsRepository.findByUserAndOtpType(fetchedUser, OTPType.LOGIN);
        if (fetchedOTP.isEmpty()) {
            return new ResponseEntity<>("Please login again using Email and Password", HttpStatus.UNAUTHORIZED);
        }
        OTPs fetchedOTP1 = fetchedOTP.get();
        if (new Date().after(fetchedOTP1.getExpiryDate())) {
            return new ResponseEntity<>("OTP has expired", HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(fetchedOTP1.getOtp(), otp)) {
            String jwtToken = jwtUtility.generateToken(email);
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtToken);
            response.put("message", "Signed in successfully!");
            otPsRepository.deleteById(fetchedOTP1.getId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>("Incorrect OTP", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping(Endpoints.getUser)
    ResponseEntity<Object> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(Endpoints.initResetPassword)
    ResponseEntity<Object> initResetPassword(@RequestParam String email) {
        try {
            if (email.isEmpty()) {
                return new ResponseEntity<>("Missing Email in requests", HttpStatus.FORBIDDEN);
            }
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.FORBIDDEN);
            }
            otPsRepository.deleteByUserAndOtpType(user, OTPType.RESET_PASSWORD);
            OTPs otp = this.createOtp(user, OTPType.RESET_PASSWORD);
            otPsRepository.save(otp);
            this.sendEmail(email, "OTP to Reset your Password", "Your OTP is \n" + otp.getOtp());
            return new ResponseEntity<>("OTP is sent!", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Something Went Wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(Endpoints.setNewPassword)
    ResponseEntity<Object> setNewPassword(@RequestBody SetNewPasswordReq body) {

        if (body.otp == null || body.email == null || body.newPassword == null || body.email.isBlank() || body.otp.isBlank() || body.newPassword.isBlank()) {
            return new ResponseEntity<>("Missing Email or OTP or Password!", HttpStatus.FORBIDDEN);
        }

        User user = userRepository.findByEmail(body.email);
        if (user == null) {
            return new ResponseEntity<>("Invalid Email", HttpStatus.FORBIDDEN);
        }
        Optional<OTPs> storedOtp = otPsRepository.findByUserAndOtpType(user, OTPType.RESET_PASSWORD);
        if (storedOtp.isEmpty())
            return new ResponseEntity<>("Invalid OTP", HttpStatus.FORBIDDEN);
        if (storedOtp.get().getExpiryDate().before(new Date())) {
            return new ResponseEntity<>("OTP is Expired", HttpStatus.FORBIDDEN);
        }
        if (storedOtp.get().getOtp().matches(body.otp)) {
            otPsRepository.deleteById(storedOtp.get().getId());
            user.setPassword(passwordEncoder.encode(body.newPassword));
            userRepository.save(user);
            return new ResponseEntity<>("Successfully reset the password", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("Incorrect OTP", HttpStatus.FORBIDDEN);
    }
}
