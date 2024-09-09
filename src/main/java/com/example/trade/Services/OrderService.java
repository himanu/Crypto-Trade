package com.example.trade.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderService {

    @Autowired
    CoinServiceImpl coinService;
    public boolean buyOrder(String coinId, BigDecimal quantity) throws JsonProcessingException {
        String coinDetail = coinService.getCoinDetails(coinId);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode =  objectMapper.readTree(coinDetail);
        double price = jsonNode.get("market_data").get("current_price").get("usd").asDouble();
        System.out.println("price " + price);
        return true;
    }
}
