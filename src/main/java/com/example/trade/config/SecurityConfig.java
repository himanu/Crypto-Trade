package com.example.trade.config;

import com.example.trade.domain.Endpoints;
import com.example.trade.domain.JwtFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(new CustomRequestMatcher()).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, BasicAuthenticationFilter.class)
                .csrf((csrf) -> csrf.disable());
        return http.build();
    }
}

class CustomRequestMatcher implements RequestMatcher {

    private final Map<String, Boolean> endpointMap = Endpoints.privateEndpoint;

    @Override
    public boolean matches(HttpServletRequest request) {
        String url = request.getRequestURI();
        Boolean isPrivate = endpointMap.getOrDefault(url, true);
        return !isPrivate;
    }
}

