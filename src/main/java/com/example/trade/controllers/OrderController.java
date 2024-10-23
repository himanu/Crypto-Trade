package com.example.trade.controllers;

import com.example.trade.DTOs.CreateOrderReq;
import com.example.trade.Services.OrderService;
import com.example.trade.domain.Endpoints;
import com.example.trade.domain.OrderStatus;
import com.example.trade.domain.OrderType;
import com.example.trade.entities.Orders;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping(Endpoints.buyOrder)
    ResponseEntity<String> buyOrder(@RequestParam String coinId, @RequestParam BigDecimal amount) throws Exception {
        Orders order = orderService.buyOrder(coinId, amount);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
