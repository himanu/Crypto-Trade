package com.example.trade.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class General {
    @GetMapping("/")
    String helloWorld() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Hello! " + auth.getName();
    }
}
