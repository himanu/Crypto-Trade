package com.example.trade.entities;

import com.example.trade.domain.OTPType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "otps", uniqueConstraints = {
        // Combination of user_Id and otp_type is unique
        @UniqueConstraint(columnNames = {"user_id", "otp_type"})
})
public class OTPs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String otp;

    @ManyToOne
    private User user;

    private OTPType otpType;

    private Date createdDate;

    private Date expiryDate;
}
