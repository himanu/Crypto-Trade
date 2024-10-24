package com.example.trade.domain;

import java.util.HashMap;
import java.util.Map;

public class Endpoints {
    public static final String login = "/login";
    public static final String signup = "/signup";
    public static final String enable2FA = "/enable_2fa";
    public static final String otpVerify = "/opt_verify";
    public static final String getUser = "/get_user";
    public static final String initResetPassword = "/init_reset_password";
    public static final String setNewPassword = "/set_new_password";
    public static final String getCoins = "/coin/list";
    public static final String getMarketChart = "/coin/market/chart";
    public static final String getCoinDetails = "/coin/detail";
    public static final String searchCoin = "/coin/search";
    public static final String top50Coin = "/coin/top50";
    public static final String trendingCoins = "/coin/trending";
    public static final String topLosers = "/coin/top_losers";
    public static final String topGainers = "/coin/top_gainers";
    public static final String buyOrder = "/order/buy";
    public static final String sellOrder = "/order/sell";
    public static final String createOrder = "/order/create";
    public static final String orderHistory = "/order/history";
    public static final String getPortfolio = "get/portfolio";
    public static final String addMoney = "/wallet/add";
    public static final String withdrawMoney = "/wallet/withdraw";
    public static final String getBalance = "/wallet/balance";
    public static final String createWallet = "/wallet/create";
    public static final String getWallet = "/wallet";
    public static final String getWalletTxns = "/wallet/txns";
    public static final String initiateDeposit = "/wallet/deposit/init";
    public static final String verifyDeposit = "/wallet/deposit/verify";
    public static final String portfolio = "/portfolio";
    public static final String activity = "/activity";
    public static final String helloWorld = "/";
    public static  Map<String, Boolean> privateEndpoint = new HashMap<>();

    static {
        privateEndpoint.put(login, false);
        privateEndpoint.put(signup, false);
        privateEndpoint.put(enable2FA, true);
        privateEndpoint.put(otpVerify, false);
        privateEndpoint.put(getUser, true);
        privateEndpoint.put(initResetPassword, false);
        privateEndpoint.put(setNewPassword, false);
        privateEndpoint.put(getCoins, true);
        privateEndpoint.put(getMarketChart, true);
        privateEndpoint.put(getCoinDetails, true);
        privateEndpoint.put(searchCoin, true);
        privateEndpoint.put(top50Coin, true);
        privateEndpoint.put(trendingCoins, true);
        privateEndpoint.put(topGainers, true);
        privateEndpoint.put(topLosers, true);
        privateEndpoint.put(createOrder, true);
        privateEndpoint.put(orderHistory, true);
        privateEndpoint.put(getPortfolio, true);
        privateEndpoint.put(addMoney, true);
        privateEndpoint.put(withdrawMoney, true);
        privateEndpoint.put(getBalance, true);
        privateEndpoint.put(createWallet, true);
        privateEndpoint.put(helloWorld, false);
        privateEndpoint.put(getWallet, true);
        privateEndpoint.put(getWalletTxns, true);
        privateEndpoint.put(initiateDeposit, true);
        privateEndpoint.put(verifyDeposit, true);
        privateEndpoint.put(buyOrder, true);
        privateEndpoint.put(sellOrder, true);
        privateEndpoint.put(portfolio, true);
        privateEndpoint.put(activity, true);
    }
}
