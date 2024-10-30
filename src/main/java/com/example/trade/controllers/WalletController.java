package com.example.trade.controllers;

import com.example.trade.DTOs.CreateOrderReq;
import com.example.trade.DTOs.VerifyDepositReq;
import com.example.trade.Services.UserService;
import com.example.trade.Services.WalletService;
import com.example.trade.domain.Endpoints;
import com.example.trade.domain.OrderStatus;
import com.example.trade.domain.OrderType;
import com.example.trade.domain.WalletTxnType;
import com.example.trade.entities.Orders;
import com.example.trade.entities.User;
import com.example.trade.entities.Wallet;
import com.example.trade.entities.WalletTxns;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@RestController
public class WalletController {
    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @PutMapping(Endpoints.initiateDeposit)
    ResponseEntity<Object> initiateDeposit (@RequestParam double amount) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient("rzp_test_4nK1prd83ssqFL", "HvqJexBcjxiX6W6ejOdfPRKJ");
        User user = userService.getUser();
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount*100);
        orderRequest.put("currency", "USD");
        orderRequest.put("receipt", "receipt#1");
        JSONObject notes = new JSONObject();
        notes.put("notes_key_1", "Tea, Earl Grey, Hot");
        orderRequest.put("notes", notes);

        Order order = razorpay.orders.create(orderRequest);
        walletService.initiateDeposit(BigDecimal.valueOf(amount), user, order.get("id"));
        HashMap<String, String> response = new HashMap<>();
        response.put("id", order.get("id"));
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(Endpoints.verifyDeposit)
    ResponseEntity<Object> verifyDeposit(@RequestBody VerifyDepositReq verifyDepositReq) throws RazorpayException {
        String paymentId = verifyDepositReq.getRazorpayPaymentId();
        String razorpayOrderId = verifyDepositReq.getRazorpayOrderId();
        String signature = verifyDepositReq.getRazorpaySignature();
        String orderId = verifyDepositReq.getOrderId();


        try {
            String secret = "HvqJexBcjxiX6W6ejOdfPRKJ";
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);
            boolean status =  Utils.verifyPaymentSignature(options, secret);
            HashMap<String, Object> response = new HashMap<>();
            if (status) {
                WalletTxns walletTxns = walletService.updateWalletTxnStatus(orderId, OrderStatus.succeed, razorpayOrderId, paymentId);
                walletService.addFunds(walletTxns.getAmount(), userService.getUser());
                response.put("message", "Payment verified successfully");
                response.put("amount", walletTxns.getWallet().getBalance());
                return ResponseEntity.ok(response);
            } else {
                walletService.updateWalletTxnStatus(orderId, OrderStatus.failed, razorpayOrderId, paymentId);
                response.put("message", "Payment verification failed: Invalid signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            walletService.updateWalletTxnStatus(orderId, OrderStatus.failed, razorpayOrderId, paymentId);
            HashMap<String, Object> response = new HashMap<>();
            response.put("message", "Error during payment verification");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PostMapping(Endpoints.withdrawMoney)
    ResponseEntity<Object> withdrawMoney(@RequestParam double amount) throws Exception {
        Wallet wallet = walletService.withDrawFunds(BigDecimal.valueOf(amount));
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }
    @GetMapping(Endpoints.getBalance)
    ResponseEntity<BigDecimal> getBalance() {
        User user = userService.getUser();
        BigDecimal balance = walletService.getUserBalance(user);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @PostMapping(Endpoints.createWallet)
    ResponseEntity<Wallet> createWallet() {
        User user = userService.getUser();
        Wallet wallet = walletService.createWallet(user);
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }
    @GetMapping(Endpoints.getWallet)
    ResponseEntity<Wallet> getWallet() {
        User user = userService.getUser();
        Wallet wallet = walletService.getUserWallet(user);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }

    @GetMapping(Endpoints.getWalletTxns)
    ResponseEntity<List<WalletTxns>> getWalletTxns() {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coingecko.com/api/v3/ping";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);

        ResponseEntity<String> coinsResponse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        System.out.println("coinsResponse" + coinsResponse);
        User user = userService.getUser();
        List<WalletTxns> txns = walletService.getWalletTxns(user);
        return new ResponseEntity<>(txns, HttpStatus.OK);
    }
}
