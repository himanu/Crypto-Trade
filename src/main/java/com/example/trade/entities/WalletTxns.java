package com.example.trade.entities;

import com.example.trade.domain.WalletTxnType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class WalletTxns {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne
    Wallet wallet;

    @Enumerated(EnumType.STRING)
    WalletTxnType walletTxnType;

    LocalDateTime localDateTime = LocalDateTime.now();

    String orderId;

    BigDecimal amount;
}
