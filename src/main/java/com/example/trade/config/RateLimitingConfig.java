package com.example.trade.config;

import com.example.trade.domain.Endpoints;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

// Create buckets for rate limiting
@Configuration
public class RateLimitingConfig {

    @Bean
    public Map<String, Bucket> setUpRateLimitBuckets() {
        Map<String, Bucket> buckets = new HashMap<>();
        buckets.put(Endpoints.enable2FA, createBucket(100, 10));
        buckets.put(Endpoints.getUser, createBucket(100, 20));
        buckets.put(Endpoints.login, createBucket(100, 10));
        buckets.put(Endpoints.signup, createBucket(100, 10));
        buckets.put(Endpoints.otpVerify, createBucket(100, 10));
        buckets.put(Endpoints.initResetPassword, createBucket(100, 10));
        buckets.put(Endpoints.setNewPassword, createBucket(100, 10));
        buckets.put(Endpoints.getCoins, createBucket(100, 10));
        buckets.put(Endpoints.getMarketChart, createBucket(100, 10));
        buckets.put(Endpoints.getCoinDetails, createBucket(100, 10));
        buckets.put(Endpoints.searchCoin, createBucket(100, 10));
        buckets.put(Endpoints.trendingCoins, createBucket(100, 10));
        buckets.put(Endpoints.top50Coin, createBucket(100, 10));
        buckets.put(Endpoints.createOrder, createBucket(100, 10));
        buckets.put(Endpoints.orderHistory, createBucket(100, 10));
        buckets.put(Endpoints.getPortfolio, createBucket(100, 10));
        buckets.put(Endpoints.addMoney, createBucket(100, 10));
        buckets.put(Endpoints.withdrawMoney, createBucket(100, 10));
        buckets.put(Endpoints.getBalance, createBucket(100, 10));
        buckets.put(Endpoints.createWallet, createBucket(100, 10));
        buckets.put("/", createBucket(100, 10));
        return buckets;
    }

    private Bucket createBucket(int capacity, int refillQtyPerMinute) {
        Refill refill = Refill.intervally(refillQtyPerMinute, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}