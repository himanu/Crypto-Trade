package com.example.trade.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(
        name = "holdings",
        indexes = {
                @Index(name = "idx_user_qty", columnList = "user_id, qty")
        }
)

public class Holdings {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne
    User user;

    String coinId;

    @Column(precision = 10, scale = 4)
    BigDecimal avgPrice;

    @Column(precision = 10, scale = 6)
    BigDecimal qty;
}
