package com.example.trade.entities;

import com.example.trade.domain.OrderStatus;
import com.example.trade.domain.OrderType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne
    User user;

    OrderType orderType;

    String coinId;

    LocalDateTime timeStamp = LocalDateTime.now();

    BigDecimal quantity;

    BigDecimal txnPrice;

    OrderStatus orderStatus;
}
