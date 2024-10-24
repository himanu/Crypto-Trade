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
import java.math.RoundingMode;
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
        System.out.println(txnPrice);
        Orders order = new Orders();
        order.setOrderStatus(orderStatus);
        order.setOrderType(orderType);
        order.setUser(user);
        order.setCoinId(coinId);
        order.setTxnPrice(txnPrice);
        order.setQuantity(quantity);
        order.setRemQty(remQty);
        ordersRepository.save(order);
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
        BigDecimal price = coinService.getCoinLatestPrize(coinId).setScale(6, RoundingMode.HALF_UP);

        // calculate qty
        BigDecimal qty = BigDecimal.valueOf(amount.doubleValue()/price.doubleValue()).setScale(6, RoundingMode.HALF_UP);
        // create a buy order
        System.out.println("QTY " + qty + " price " + price);
        Orders order = createOrder(user, OrderType.buy, coinId, qty, qty, price, OrderStatus.succeed);
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
            newHolding.setAvgPrice(price);
            holdingRepository.save(newHolding);
        } else {
            // update holdings
            holdings.setQty(holdings.getQty().add(qty));
            BigDecimal newAvgPrice;
            double currentAvgPrice = holdings.getAvgPrice().doubleValue();
            double currentQty = holdings.getQty().doubleValue();
            double coinPrice = price.doubleValue();
            double newQty = qty.doubleValue();
            newAvgPrice = BigDecimal.valueOf(((currentAvgPrice*currentQty + coinPrice * newQty)/(currentQty + newQty))).setScale(6, RoundingMode.HALF_UP);
            holdings.setAvgPrice(newAvgPrice);
            holdingRepository.save(holdings);
        }
        return order;
    }

    public Orders createFailedOrder(String coinId, BigDecimal amount) throws JsonProcessingException {
        User user = userService.getUser();
        BigDecimal price = coinService.getCoinLatestPrize(coinId);
        BigDecimal qty = BigDecimal.valueOf(amount.doubleValue()/price.doubleValue()).setScale(6, RoundingMode.HALF_UP);
        return createOrder(user, OrderType.buy, coinId, qty, qty, price, OrderStatus.failed);
    }
}
