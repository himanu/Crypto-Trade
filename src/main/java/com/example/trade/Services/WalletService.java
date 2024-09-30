package com.example.trade.Services;

import com.example.trade.domain.WalletTxnType;
import com.example.trade.entities.User;
import com.example.trade.entities.Wallet;
import com.example.trade.entities.WalletTxns;
import com.example.trade.repositories.WalletRepository;
import com.example.trade.repositories.WalletTxnRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WalletService {
    @Autowired
    WalletRepository walletRepository;

    @Autowired
    UserService userService;

    @Autowired
    WalletTxnRepository walletTxnRepository;

    public BigDecimal getUserBalance(User user) {
        return walletRepository.findBalanceByUser(user);
    }

    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.valueOf(0));
        return walletRepository.save(wallet);
    }

    void addFunds(BigDecimal amount, User user, WalletTxnType walletTxnType, String orderId) {
        Wallet wallet = walletRepository.findByUser(user);
        BigDecimal currentBalance = wallet.getBalance();
        wallet.setBalance(currentBalance.add(amount));
        walletRepository.save(wallet);
        createWalletTxn(wallet, walletTxnType, amount, orderId);
    }

    @Transactional
    public void addMoney(BigDecimal amount) {
        User user = userService.getUser();
        addFunds(amount, user, WalletTxnType.add_funds, null);
    }

    void deductFunds(BigDecimal amount, User user, WalletTxnType walletTxnType, String orderId) {
        Wallet wallet = walletRepository.findByUser(user);
        BigDecimal currentBalance = getUserBalance(user);
        if (currentBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Not Sufficient Balance!");
        }
        wallet.setBalance(currentBalance.subtract(amount));
        walletRepository.save(wallet);
        createWalletTxn(wallet, walletTxnType, amount, orderId);
    }

    void createWalletTxn(Wallet wallet, WalletTxnType walletTxnType, BigDecimal amount, String orderId) {
        WalletTxns walletTxns = new WalletTxns();
        walletTxns.setWalletTxnType(walletTxnType);
        walletTxns.setAmount(amount);
        walletTxns.setOrderId(orderId);
        walletTxns.setWallet(wallet);
        walletTxnRepository.save(walletTxns);
    }

    public Wallet getUserWallet(User user) {
        return walletRepository.findByUser(user);
    }


}
