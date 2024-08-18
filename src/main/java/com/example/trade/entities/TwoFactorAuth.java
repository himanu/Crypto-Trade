package com.example.trade.entities;
import com.example.trade.domain.VERIFICATION_TYPE;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean isEnabled = false;
    private VERIFICATION_TYPE sendTo;
}
