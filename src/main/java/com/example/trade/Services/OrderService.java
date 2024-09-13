package com.example.trade.Services;

import com.example.trade.domain.OrderStatus;
import com.example.trade.domain.OrderType;
import com.example.trade.domain.WalletTxnType;
import com.example.trade.entities.Orders;
import com.example.trade.entities.User;
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
    OrdersRepository ordersRepository;

    private Orders createOrder(User user, OrderType orderType, String coinId, BigDecimal quantity, BigDecimal txnPrice, OrderStatus orderStatus) {
        Orders order = new Orders();
        order.setOrderStatus(orderStatus);
        order.setOrderType(orderType);
        order.setUser(user);
        order.setCoinId(coinId);
        order.setTxnPrice(txnPrice);
        order.setQuantity(quantity);
        return order;
    }

    @Transactional
    public Orders buyOrder(String coinId, BigDecimal quantity) throws JsonProcessingException {
        User user = userService.getUser();
        double price = coinService.getCoinLatestPrize(coinId);
        System.out.println("price " + price);
        Orders order = createOrder(user, OrderType.buy, coinId, quantity, BigDecimal.valueOf(price), OrderStatus.pending);
        BigDecimal value = quantity.multiply(BigDecimal.valueOf(price));

        BigDecimal balance = walletService.getUserBalance(user);
        System.out.println("user balance " + balance);
        try {
            if (balance.compareTo(value) < 0){
                order.setOrderStatus(OrderStatus.failed);
                order.setRemark("Insufficient Balance. Needs " + value);
                ordersRepository.save(order);
            } else {
                order.setOrderStatus(OrderStatus.succeed);
                ordersRepository.save(order);
                walletService.deductFunds(value, user, WalletTxnType.bought_asset, String.valueOf(order.getId()));
            }
            return order;
        } catch(Exception e) {
            order.setOrderStatus(OrderStatus.failed);
            order.setRemark(e.getMessage());
            ordersRepository.save(order);
            return order;
        }
    }
}
