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
import java.util.stream.Collectors;
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
        List<Holdings> holdings = holdingRepository.findAllByUser(user);
        for (Holdings holding: holdings) {
            String coinId = holding.getCoinId();
//            String coinUrl = "https://coin-images.coingecko.com/coins/images/1/large/" + coinId + ".png";

            String marketData = coinService.getCoinDetails(coinId);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode res = objectMapper.readTree(marketData);

            double currentValue = holding.getQty().multiply(coinService.getCoinLatestPrize(coinId)).doubleValue();
            double investedValue = holding.getQty().doubleValue() * holding.getAvgPrice().doubleValue();
            double returnValue = (currentValue - investedValue);
            double returnValuePercentage = (returnValue/investedValue)*100;
            Map<String, Object> map = new HashMap<>();
            map.put("holding", holding);
            map.put("coinUrl", res.get("image").get("small"));
            map.put("coinName", res.get("name"));
            map.put("currentValue", currentValue);
            map.put("returnValue", returnValue);
            map.put("returnValuePercentage", returnValuePercentage);

            response.add(map);
        }
        return response;
    }
}
