package com.example.trade.Services;

import com.example.trade.domain.OrderStatus;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void addFunds(BigDecimal amount, User user) {
        Wallet wallet = walletRepository.findByUser(user);
        BigDecimal currentBalance = wallet.getBalance();
        wallet.setBalance(currentBalance.add(amount));
        walletRepository.save(wallet);
    }

    @Transactional
    public void initiateDeposit(BigDecimal amount, User user, String depositOrderId) {
        Wallet wallet = walletRepository.findByUser(user);
        createWalletTxn(wallet, WalletTxnType.add_funds, amount, null, depositOrderId, OrderStatus.pending, null, null);
    }

    Wallet deductFunds(BigDecimal amount, User user, WalletTxnType walletTxnType, String orderId, String coinName, String coinImg) {
        Wallet wallet = walletRepository.findByUser(user);
        BigDecimal currentBalance = getUserBalance(user);
        if (currentBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Not Sufficient Balance!");
        }
        wallet.setBalance(currentBalance.subtract(amount));
        walletRepository.save(wallet);
        createWalletTxn(wallet, walletTxnType, amount.multiply(BigDecimal.valueOf(-1)), orderId, null, OrderStatus.succeed, coinName, coinImg);
        return wallet;
    }

    void createWalletTxn(Wallet wallet, WalletTxnType walletTxnType, BigDecimal amount, String orderId, String depositOrderId, OrderStatus orderStatus, String coinName, String coinImg) {
        WalletTxns walletTxns = new WalletTxns();
        walletTxns.setWalletTxnType(walletTxnType);
        walletTxns.setAmount(amount);
        walletTxns.setOrderId(orderId);
        walletTxns.setWallet(wallet);
        walletTxns.setDepositWithdrawOrderId(depositOrderId);
        walletTxns.setStatus(orderStatus);
        walletTxns.setCoinName(coinName);
        walletTxns.setCoinImg(coinImg);
        walletTxnRepository.save(walletTxns);
    }

    public Wallet getUserWallet(User user) {
        return walletRepository.findByUser(user);
    }

    public WalletTxns updateWalletTxnStatus(String orderId, OrderStatus orderStatus, String rzOrderId, String rzPaymentId) {
        Optional<WalletTxns> walletTxns = walletTxnRepository.findByDepositWithdrawOrderId(orderId);
        if (walletTxns.isEmpty())
            return null;
        WalletTxns walletTxns1 = walletTxns.get();
        walletTxns1.setStatus(orderStatus);
        walletTxns1.setRazorpayOrderId(rzOrderId);
        walletTxns1.setRazorpayPaymentId(rzPaymentId);
        return walletTxnRepository.save(walletTxns1);
    }

    public Wallet withDrawFunds(BigDecimal amount) throws Exception {
        User user = userService.getUser();
        return deductFunds(amount, user, WalletTxnType.withdraw_funds, null, null, null);
    }

    public List<WalletTxns> getWalletTxns(User user) {
        Wallet wallet = getUserWallet(user);
        List<WalletTxns> txns =  walletTxnRepository.findByWallet(wallet);
        return txns.stream()
                .filter(txn -> OrderStatus.succeed.equals(txn.getStatus()))
                .sorted(Comparator.comparing(WalletTxns::getLocalDateTime).reversed()) // Adjust the method name as needed
                .collect(Collectors.toList());
    }

}
