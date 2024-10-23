package com.example.trade.DTOs;

import lombok.Data;

@Data
public class VerifyDepositReq {
    private String orderId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
