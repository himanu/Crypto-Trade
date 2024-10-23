package com.example.trade.repositories;

import com.example.trade.entities.Holdings;
import com.example.trade.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoldingRepository extends JpaRepository<Holdings, Long> {
    Holdings findByUserAndCoinId(User user, String coinId);
}
