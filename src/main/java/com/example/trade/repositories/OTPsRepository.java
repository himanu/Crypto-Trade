package com.example.trade.repositories;

import com.example.trade.entities.OTPs;
import com.example.trade.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPsRepository extends JpaRepository<OTPs, Long> {
    OTPs findByUser(User user);
}
