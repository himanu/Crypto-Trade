package com.example.trade.controllers;

import com.example.trade.DTOs.CreateOrderReq;
import com.example.trade.Services.UserService;
import com.example.trade.Services.WalletService;
import com.example.trade.domain.Endpoints;
import com.example.trade.domain.OrderType;
import com.example.trade.entities.Orders;
import com.example.trade.entities.User;
import com.example.trade.entities.Wallet;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class WalletController {
    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @PostMapping(Endpoints.addMoney)
    ResponseEntity<String> addMoney(@RequestParam double amount) {
        walletService.addMoney(BigDecimal.valueOf(amount));
        return new ResponseEntity<>("Added money", HttpStatus.OK);
    }

    @GetMapping(Endpoints.getBalance)
    ResponseEntity<BigDecimal> getBalance() {
        User user = userService.getUser();
        BigDecimal balance = walletService.getUserBalance(user);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @PostMapping(Endpoints.createWallet)
    ResponseEntity<Wallet> createWallet() {
        User user = userService.getUser();
        Wallet wallet = walletService.createWallet(user);
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }
    @GetMapping(Endpoints.getWallet)
    ResponseEntity<Wallet> getWallet() {
        User user = userService.getUser();
        Wallet wallet = walletService.getUserWallet(user);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }
}
