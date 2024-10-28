package com.example.trade.entities;

import com.example.trade.domain.USER_ROLE;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @Column(name = "username", nullable = false) // Maps to "username" column
    private String username;

    @Column(name = "password", nullable = false) // Maps to "password" column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "email", nullable = false, unique = true) // Maps to "email" column
    private String email;

    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;

    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
}
