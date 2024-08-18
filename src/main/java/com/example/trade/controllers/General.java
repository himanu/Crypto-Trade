package com.example.trade.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class General {
    @GetMapping("/")
    String helloWorld() {
        return "Hello!";
    }
}
