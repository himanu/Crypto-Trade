package com.example.trade.domain;

import com.example.trade.entities.User;
import com.example.trade.repositories.UserRepository;
import io.github.bucket4j.Bucket;
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
import java.util.Map;

// What is OncePerRequestFilter?
// What are authorities, grantedAuthorities, importance of these?
// What are UsernamePasswordAuthenticationToken and why?
// what is filterChain.doFilter(request, response);
// Do the below code checks the validity of jwt specially expiry time?
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtility jwtUtility;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Map<String, Bucket> bucketMap;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorization = request.getHeader("Authorization");
            String url = request.getRequestURI();
            Bucket bucket = bucketMap.get(url);
            if (bucket == null || !bucket.tryConsume(1)) {
                System.out.println("Too many requestes ");
                response.setStatus(429);
                return;
            }
            if (!Endpoints.privateEndpoint.get(url)) {
                // for public endpoints we don't need to test authentication
                filterChain.doFilter(request, response);
                return;
            }
            if (authorization != null) {
                String token = authorization.substring(7);
                String email = jwtUtility.extractUsername(token);
                User fetchedUser = userRepository.findByEmail(email);
                if (fetchedUser != null) {
                    Authentication auth = new UsernamePasswordAuthenticationToken(email,
                            null,
                            new ArrayList<>()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    filterChain.doFilter(request, response);
                    return;
                }
            }
            response.setStatus(401);
        } catch (Exception e) {
            response.setStatus(401);
        }
    }
}
