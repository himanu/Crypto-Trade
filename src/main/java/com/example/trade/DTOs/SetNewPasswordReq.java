package com.example.trade.DTOs;

import lombok.Data;

@Data
public class SetNewPasswordReq {
    public String email;
    public String otp;
    public String newPassword;
}
