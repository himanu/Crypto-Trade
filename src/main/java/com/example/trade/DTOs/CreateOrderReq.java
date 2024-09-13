package com.example.trade.DTOs;

import com.example.trade.domain.OrderType;

public class CreateOrderReq {
    public OrderType orderType;
    public String coinId;
    public double qty;
}
