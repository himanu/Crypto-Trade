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
    }
}
