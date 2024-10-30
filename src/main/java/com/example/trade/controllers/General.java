package com.example.trade.controllers;

import com.example.trade.Services.HoldingService;
import com.example.trade.domain.Endpoints;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class General {
    @Autowired
    HoldingService holdingService;
    @GetMapping("/")
    String helloWorld() {
        return "Hello! ";
    }

    @GetMapping("/api")
    String coinAPI() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coingecko.com/api/v3/ping";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        httpHeaders.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);

        ResponseEntity<String> coinsResponse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        System.out.println("coinsResponse" + coinsResponse);
        return "Hello! ";
    }

    @GetMapping("/apiv1")
    String coin() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coindesk.com/v1/bpi/currentprice.json";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        httpHeaders.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);

        ResponseEntity<String> coinsResponse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        System.out.println("coinsResponse" + coinsResponse);
        return "Hello! ";
    }

    @GetMapping(Endpoints.portfolio)
    Object getPortfolio() throws JsonProcessingException {
        return holdingService.getHoldings();
    }
}
