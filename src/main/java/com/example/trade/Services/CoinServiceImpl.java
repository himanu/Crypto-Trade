package com.example.trade.Services;

import com.example.trade.entities.Coin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CoinServiceImpl implements CoinService{
    @Autowired
    private Environment environment;

    private String coin_gecko_api_key;

    @PostConstruct
    public void init() {
        this.coin_gecko_api_key = environment.getProperty("coin_api_key");
    }

    @Override
    public List<Coin> getCoinList(int page) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=20&page=" + page;
        System.out.println("url " + url);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        httpHeaders.add("x_cg_demo_api_key", this.coin_gecko_api_key);
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);

        ResponseEntity<String> coinsResponse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Register module for Java 8 date/time
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.readValue(coinsResponse.getBody(), mapper.getTypeFactory().constructCollectionType(List.class, Coin.class));
    }

    @Override
    public String getMarketChart(String coinId, int days) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId + "/market_chart?vs_currency=usd&days=" + days + "&x_cg_demo_api_key=" + this.coin_gecko_api_key;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);

        ResponseEntity<String> coinsResponse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);


        return coinsResponse.getBody();
    }

    @Override
    public String getCoinDetails(String coinId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId + "?x_cg_demo_api_key=" + this.coin_gecko_api_key;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> coinsResponse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        return coinsResponse.getBody();
    }

    @Override
    public String searchCoin(String keyword) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coingecko.com/api/v3/search?query=" + keyword + "&x_cg_demo_api_key=" + this.coin_gecko_api_key;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> coinsResponse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        return coinsResponse.getBody();
    }

    @Override
    public String getTop50Coins() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=50&page=1&x_cg_demo_api_key=" + this.coin_gecko_api_key;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> coinsResponse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        return coinsResponse.getBody();
    }

    @Override
    public String getTrendingCoins() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coingecko.com/api/v3/search/trending" + "?x_cg_demo_api_key=" + this.coin_gecko_api_key;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> coinsResponse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        return coinsResponse.getBody();
    }

    public String getTopLosers() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coingecko.com/api/v3/search/trending" + "?x_cg_demo_api_key=" + this.coin_gecko_api_key;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> coinsResponse = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        return coinsResponse.getBody();
    }

    public BigDecimal getCoinLatestPrize(String coinId) throws JsonProcessingException {
        String coinDetail = getCoinDetails(coinId);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode =  objectMapper.readTree(coinDetail);
        return jsonNode.get("market_data").get("current_price").get("usd").decimalValue();
    }
}
