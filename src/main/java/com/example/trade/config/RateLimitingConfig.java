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
        buckets.put(Endpoints.enable2FA, createBucket(10, 10));
        buckets.put(Endpoints.getUser, createBucket(20, 20));
        buckets.put(Endpoints.login, createBucket(10, 10));
        buckets.put(Endpoints.signup, createBucket(10, 10));
        buckets.put(Endpoints.otpVerify, createBucket(10, 10));
        buckets.put(Endpoints.initResetPassword, createBucket(10, 10));
        buckets.put(Endpoints.setNewPassword, createBucket(10, 10));
        buckets.put(Endpoints.getCoins, createBucket(10, 10));
        buckets.put(Endpoints.getMarketChart, createBucket(10, 10));
        buckets.put(Endpoints.getCoinDetails, createBucket(10, 10));
        buckets.put(Endpoints.searchCoin, createBucket(10, 10));
        buckets.put(Endpoints.trendingCoins, createBucket(10, 10));
        buckets.put(Endpoints.top50Coin, createBucket(10, 10));
        buckets.put(Endpoints.createOrder, createBucket(10, 10));
        buckets.put(Endpoints.orderHistory, createBucket(10, 10));
        buckets.put(Endpoints.getPortfolio, createBucket(10, 10));
        buckets.put(Endpoints.addMoney, createBucket(10, 10));
        buckets.put(Endpoints.withdrawMoney, createBucket(10, 10));
        buckets.put(Endpoints.getBalance, createBucket(10, 10));
        buckets.put(Endpoints.createWallet, createBucket(10, 10));
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