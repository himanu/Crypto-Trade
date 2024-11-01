package com.example.trade.repositories;

import com.example.trade.entities.Holdings;
import com.example.trade.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface HoldingRepository extends JpaRepository<Holdings, Long> {
    Holdings findByUserAndCoinId(User user, String coinId);

    List<Holdings> findAllByUserAndQtyGreaterThan(User user, BigDecimal qty);
}
