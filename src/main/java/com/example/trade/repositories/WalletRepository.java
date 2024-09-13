package com.example.trade.repositories;

import com.example.trade.entities.User;
import com.example.trade.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("SELECT w.balance FROM Wallet w WHERE w.user = :user")
    BigDecimal findBalanceByUser(@Param("user") User user);

    Wallet findByUser(User user);
}
