package com.example.trade.controllers;

import com.example.trade.Services.HoldingService;
import com.example.trade.domain.Endpoints;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class General {
    @Autowired
    HoldingService holdingService;
    @GetMapping("/")
    String helloWorld() {
        return "Hello! ";
    }

    @GetMapping(Endpoints.portfolio)
    Object getPortfolio() throws JsonProcessingException {
        return holdingService.getHoldings();
    }
}
