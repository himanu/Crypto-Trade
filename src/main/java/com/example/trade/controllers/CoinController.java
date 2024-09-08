package com.example.trade.controllers;

import com.example.trade.Services.CoinServiceImpl;
import com.example.trade.domain.Endpoints;
import com.example.trade.entities.Coin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CoinController {
    @Autowired
    CoinServiceImpl coinService;

    @GetMapping(Endpoints.getCoins)
    ResponseEntity<List<Coin>> getCoins(@RequestParam int page) throws JsonProcessingException {
        List<Coin> coins = coinService.getCoinList(page);
        return new ResponseEntity<>(coins, HttpStatus.ACCEPTED);
    }

    @GetMapping(Endpoints.getMarketChart)
    ResponseEntity<JsonNode> getMarketChart(@RequestParam int days, @RequestParam String coinId) throws JsonProcessingException {
        String marketData = coinService.getMarketChart(coinId, days);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.readTree(marketData);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping(Endpoints.getCoinDetails)
    ResponseEntity<JsonNode> getCoinDetails(@RequestParam String coinId) throws JsonProcessingException {
        String marketData = coinService.getCoinDetails(coinId);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.readTree(marketData);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(Endpoints.searchCoin)
    ResponseEntity<JsonNode> searchCoin(@RequestParam String query) throws JsonProcessingException {
        String marketData = coinService.searchCoin(query);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.readTree(marketData);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(Endpoints.top50Coin)
    ResponseEntity<JsonNode> top50Coin() throws JsonProcessingException {
        String marketData = coinService.getTop50Coins();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.readTree(marketData);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(Endpoints.trendingCoins)
    ResponseEntity<JsonNode> getTrendingCoins() throws JsonProcessingException {
        String marketData = coinService.getTrendingCoins();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.readTree(marketData);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
