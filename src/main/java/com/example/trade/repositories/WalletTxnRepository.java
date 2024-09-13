package com.example.trade.repositories;

import com.example.trade.entities.WalletTxns;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTxnRepository extends JpaRepository<WalletTxns, Long> {
}