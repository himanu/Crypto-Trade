package com.example.trade.repositories;

import com.example.trade.domain.OTPType;
import com.example.trade.entities.OTPs;
import com.example.trade.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPsRepository extends JpaRepository<OTPs, Long> {
    Optional<OTPs> findByUserAndOtpType(User user, OTPType otpType);
}
