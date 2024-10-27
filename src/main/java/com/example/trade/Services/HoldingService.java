package com.example.trade.Services;

import com.example.trade.entities.Holdings;
import com.example.trade.entities.User;
import com.example.trade.repositories.HoldingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HoldingService {
    @Autowired UserService userService;

    @Autowired
    CoinServiceImpl coinService;

    @Autowired
    HoldingRepository holdingRepository;
    public List<Map<String, Object>> getHoldings() throws JsonProcessingException {
        User user = userService.getUser();
        List<Map<String, Object>> response = new ArrayList<>();
        List<Holdings> holdings = holdingRepository.findAllByUserAndQtyGreaterThan(user, BigDecimal.valueOf(0));
        for (Holdings holding: holdings) {
            String coinId = holding.getCoinId();

            String marketData = coinService.getCoinDetails(coinId);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode res = objectMapper.readTree(marketData);

            double qty = holding.getQty().doubleValue();
            double currentPrice = res.get("market_data").get("current_price").get("usd").doubleValue();
            double currentValue = qty * currentPrice;
            double investedValue = qty * holding.getAvgPrice().doubleValue();
            double returnValue = (currentValue - investedValue);
            double returnValuePercentage = investedValue == 0 ? 0 : (returnValue/investedValue)*100;
            Map<String, Object> map = new HashMap<>();
            map.put("holding", holding);
            map.put("coinUrl", res.get("image").get("small"));
            map.put("coinName", res.get("name"));
            map.put("currentValue", currentValue);
            map.put("returnValue", returnValue);
            map.put("returnValuePercentage", returnValuePercentage);
            map.put("coinSymbol", res.get("symbol"));
            map.put("coinPrice", currentPrice);
            map.put("coinPriceChange", res.get("market_data").get("price_change_24h"));
            map.put("coinPriceChangePercentage", res.get("market_data").get("price_change_percentage_24h"));

            response.add(map);
        }
        return response;
    }
}
