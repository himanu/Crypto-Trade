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
    ResponseEntity<Object> buyOrder(@RequestParam String coinId, @RequestParam BigDecimal amount) throws Exception {
        try {
            Orders order = orderService.buyOrder(coinId, amount);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception e) {
            orderService.createFailedOrder(coinId, amount, null, OrderType.buy);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(Endpoints.sellOrder)
    ResponseEntity<Object> sellOrder(@RequestParam String coinId, @RequestParam BigDecimal qty) throws Exception {
        try {
            Orders order = orderService.sellOrder(coinId, qty);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception e) {
            orderService.createFailedOrder(coinId, null, qty, OrderType.sell);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
