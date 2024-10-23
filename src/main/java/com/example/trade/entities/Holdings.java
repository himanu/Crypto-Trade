package com.example.trade.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Holdings {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne
    User user;

    String coinId;

    BigDecimal txnValue;

    BigDecimal qty;
}
