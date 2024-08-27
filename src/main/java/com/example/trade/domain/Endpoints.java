package com.example.trade.domain;

import java.util.HashMap;
import java.util.Map;

public class Endpoints {
    public static final String login = "/login";
    public static final String signup = "/signup";
    public static final String enable2FA = "/enable_2fa";
    public static final String otpVerify = "/opt_verify";
    public static final String getUser = "/get_user";
    public static  Map<String, Boolean> privateEndpoint = new HashMap<>();

    static {
        privateEndpoint.put(login, false);
        privateEndpoint.put(signup, false);
        privateEndpoint.put(enable2FA, true);
        privateEndpoint.put(otpVerify, false);
        privateEndpoint.put(getUser, true);
    }
}
