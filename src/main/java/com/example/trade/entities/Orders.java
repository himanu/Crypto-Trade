package com.example.trade.entities;

import com.example.trade.domain.OrderStatus;
import com.example.trade.domain.OrderType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne
    User user;

    @Enumerated(EnumType.STRING)
    OrderType orderType;

    String coinId;

    LocalDateTime timeStamp = LocalDateTime.now();

    @Column(precision = 10, scale = 6)
    BigDecimal quantity;

    @Column(precision = 10, scale = 6)
    BigDecimal remQty;

    BigDecimal txnPrice;

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

    String remark;
}
