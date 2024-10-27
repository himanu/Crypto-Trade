package com.example.trade.repositories;

import com.example.trade.domain.OrderStatus;
import com.example.trade.entities.Orders;
import com.example.trade.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    public List<Orders> findAllByUserAndOrderStatus(User user, OrderStatus orderStatus);
}
