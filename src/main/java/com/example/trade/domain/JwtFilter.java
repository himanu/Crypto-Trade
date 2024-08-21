package com.example.trade.domain;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

// What is OncePerRequestFilter?
// What are authorities, grantedAuthorities, importance of these?
// What are UsernamePasswordAuthenticationToken and why?
// what is filterChain.doFilter(request, response);
// Do the below code checks the validity of jwt specially expiry time?
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtility jwtUtility;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            String token = authorization.substring(7);
            String email = jwtUtility.extractUsername(token);
            Authentication auth = new UsernamePasswordAuthenticationToken(email,
                    null,
                    new ArrayList<>()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
