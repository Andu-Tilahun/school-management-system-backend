package com.schoolmanagment.apigateway.filter;


import com.schoolmanagment.commonsecurity.util.JwtService;
import com.schoolmanagment.commonsecurity.util.TokenBlacklistService;
import com.schoolmanagment.commonsecurity.util.UserStatusCache;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserStatusCache userStatusCache;
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/login",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/files"
    );


    public AuthenticationFilter(JwtService jwtUtil, TokenBlacklistService tokenBlacklistService, UserStatusCache userStatusCache) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userStatusCache = userStatusCache;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Invalid authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            if (jwtUtil.isTokenExpired(token)) {
                return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
            }

            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                return onError(exchange, "Token is blacklisted", HttpStatus.UNAUTHORIZED);
            }

            final String userId = jwtUtil.extractUserId(token);
            if (userStatusCache.isUserInactive(userId)) {
                throw new IllegalAccessException("User is inactive or banned. Access denied.");
            }
            return chain.filter(exchange);
        } catch (Exception e) {
            return onError(exchange, "Token validation failed", HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
