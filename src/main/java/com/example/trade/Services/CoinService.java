package com.example.trade.Services;

import com.example.trade.entities.Coin;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface CoinService {
    List<Coin> getCoinList(int page) throws JsonProcessingException;

    String getMarketChart(String coinId, int days);

    String getCoinDetails(String coinId);

    String searchCoin(String keyword);

    String getTop50Coins() throws JsonProcessingException;

    String getTrendingCoins() throws JsonProcessingException;
}
