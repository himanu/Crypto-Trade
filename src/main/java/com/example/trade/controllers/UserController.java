package com.example.trade.controllers;

import com.example.trade.domain.JwtUtility;
import com.example.trade.entities.User;
import com.example.trade.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    JwtUtility jwtUtility;

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
            String jwtToken = jwtUtility.generateToken(email);
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtToken);
            response.put("message", "Signed in successfully!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
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
}
