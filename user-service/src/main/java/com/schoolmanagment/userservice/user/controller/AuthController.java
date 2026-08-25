package com.schoolmanagment.userservice.user.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonsecurity.util.JwtService;
import com.schoolmanagment.commonsecurity.util.TokenBlacklistService;
import com.schoolmanagment.userservice.user.dto.*;
import com.schoolmanagment.userservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDto userDto = userService.getUserByUsername(request.getUsername());

            var policyNames = userDto.getEffectivePolicyNames() != null
                    ? userDto.getEffectivePolicyNames()
                    : Collections.<String>emptySet();

            String token = jwtService.generateToken(userDto.getId().toString(), policyNames, userDto.getExternalId());

            String refreshToken = jwtService.generateRefreshToken(userDto.getId().toString(), policyNames, userDto.getExternalId());

            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .user(userDto)
                    .build();

            ApiResponse response = ApiResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .data(authResponse)
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadRequestException("Invalid username or password");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // Validate it's a refresh token
            if (!jwtService.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid refresh token");
            }

            // Check if token is blacklisted
            if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token is blacklisted");
            }

            // Validate token
            if (!jwtService.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token expired or invalid");
            }

            String userId = jwtService.extractUserId(refreshToken);
            UserDto userDto = userService.getUserById(UUID.fromString(userId));
            var policyNames = userDto.getEffectivePolicyNames() != null
                    ? userDto.getEffectivePolicyNames()
                    : Collections.<String>emptySet();
            String newToken = jwtService.generateToken(userDto.getId().toString(), policyNames, userDto.getExternalId());
            String newRefreshToken = jwtService.generateRefreshToken(userDto.getId().toString(), policyNames, userDto.getExternalId());
            Long expirationTime = jwtService.extractExpiration(refreshToken).getTime() - System.currentTimeMillis();
            tokenBlacklistService.blacklistToken(refreshToken, expirationTime);

            AuthResponse response = AuthResponse.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .user(userDto)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Failed to refresh token: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Calculate remaining time until token expiration
            Long expirationTime = jwtService.extractExpiration(token).getTime() - System.currentTimeMillis();

            // Blacklist the token
            tokenBlacklistService.blacklistToken(token, expirationTime);

            ApiResponse response = ApiResponse.builder()
                    .success(true)
                    .message("Logged out successfully")
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);
        }
        throw new BadRequestException("Invalid token");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        userService.createPasswordResetToken(request.getEmail());

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Password reset link sent to your email")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

        userService.resetPassword(request.getToken(), request.getNewPassword());

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Password reset successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                throw new BadRequestException("Token is blacklisted");
            }

            if (!jwtService.isTokenExpired(token)) {
                String userId = jwtService.extractUserId(token);
                UserDto user = userService.getUserById(UUID.fromString(userId));
                ApiResponse response = ApiResponse.builder()
                        .success(true)
                        .data(user)
                        .message("Token is valid")
                        .timestamp(LocalDateTime.now())
                        .build();

                return ResponseEntity.ok(response);
            }
        }
        throw new BadRequestException("Invalid token");
    }
}
