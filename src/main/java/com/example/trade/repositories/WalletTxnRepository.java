package com.example.trade.repositories;

import com.example.trade.entities.Wallet;
import com.example.trade.entities.WalletTxns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletTxnRepository extends JpaRepository<WalletTxns, Long> {
    List<WalletTxns> findByWallet(Wallet wallet);
    Optional<WalletTxns> findByDepositWithdrawOrderId(String orderId);
}
