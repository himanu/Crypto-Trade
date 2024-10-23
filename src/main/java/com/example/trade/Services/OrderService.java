package com.example.trade.Services;

import com.example.trade.domain.OrderStatus;
import com.example.trade.domain.OrderType;
import com.example.trade.domain.WalletTxnType;
import com.example.trade.entities.Holdings;
import com.example.trade.entities.Orders;
import com.example.trade.entities.User;
import com.example.trade.entities.Wallet;
import com.example.trade.repositories.HoldingRepository;
import com.example.trade.repositories.OrdersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.ManyToOne;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    CoinServiceImpl coinService;

    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Autowired
    HoldingRepository holdingRepository;

    @Autowired
    OrdersRepository ordersRepository;

    private Orders createOrder(User user, OrderType orderType, String coinId, BigDecimal quantity, BigDecimal remQty, BigDecimal txnPrice, OrderStatus orderStatus) {
        Orders order = new Orders();
        order.setOrderStatus(orderStatus);
        order.setOrderType(orderType);
        order.setUser(user);
        order.setCoinId(coinId);
        order.setTxnPrice(txnPrice);
        order.setQuantity(quantity);
        order.setRemQty(remQty);
        return order;
    }

    @Transactional
    public Orders buyOrder(String coinId, BigDecimal amount) throws JsonProcessingException, Exception {
        // check amount needed
        User user = userService.getUser();
        Wallet wallet = walletService.getUserWallet(user);

        if (amount.compareTo(wallet.getBalance()) > 0) {
            throw new Exception("InSufficient Balance");
        }
        double price = coinService.getCoinLatestPrize(coinId);
        // calculate qty
        BigDecimal qty = amount.divide(BigDecimal.valueOf(price), 3);

        // create a buy order
        Orders order = createOrder(user, OrderType.buy, coinId, qty, qty, BigDecimal.valueOf(price), OrderStatus.succeed);
        // update wallet
        walletService.deductFunds(amount, user, WalletTxnType.bought_asset, String.valueOf(order.getId()));
        // create/update holding
        Holdings holdings = holdingRepository.findByUserAndCoinId(user, coinId);
        if (holdings == null) {
            // create holding
            Holdings newHolding = new Holdings();
            newHolding.setCoinId(coinId);
            newHolding.setQty(qty);
            newHolding.setUser(user);
            newHolding.setTxnValue(amount);
            holdingRepository.save(newHolding);
        } else {
            // update holdings
            holdings.setQty(holdings.getQty().add(qty));
            holdings.setTxnValue(holdings.getTxnValue().add(amount));
            holdingRepository.save(holdings);
        }
        return order;
    }
}
