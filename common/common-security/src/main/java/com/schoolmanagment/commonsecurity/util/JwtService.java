package com.schoolmanagment.commonsecurity.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtService {

    public static final String CLAIM_EXTERNAL_ID = "externalId";
    public static final String CLAIM_USERNAME = "username";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("tokenType"));
        } catch (Exception e) {
            return false;
        }
    }

    public String generateToken(String userId) {
        return generateToken(userId, null, null);
    }

    public String generateToken(String userId, UUID externalId) {
        return generateToken(userId, externalId, null);
    }

    public String generateToken(String userId, UUID externalId, String username) {
        Map<String, Object> claims = new HashMap<>();
        putUuidClaim(claims, CLAIM_EXTERNAL_ID, externalId);
        putStringClaim(claims, CLAIM_USERNAME, username);
        return createToken(claims, userId, expiration);
    }

    public String generateRefreshToken(String userId) {
        return generateRefreshToken(userId, null, null);
    }

    public String generateRefreshToken(String userId, UUID externalId) {
        return generateRefreshToken(userId, externalId, null);
    }

    public String generateRefreshToken(String userId, UUID externalId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        putUuidClaim(claims, CLAIM_EXTERNAL_ID, externalId);
        putStringClaim(claims, CLAIM_USERNAME, username);
        return createToken(claims, userId, refreshExpiration);
    }

    public UUID extractExternalId(String token) {
        return extractUuidClaim(token, CLAIM_EXTERNAL_ID);
    }

    public String extractUsername(String token) {
        return extractStringClaim(token, CLAIM_USERNAME);
    }

    private void putUuidClaim(Map<String, Object> claims, String claimName, UUID value) {
        if (value != null) {
            claims.put(claimName, value.toString());
        }
    }

    private void putStringClaim(Map<String, Object> claims, String claimName, String value) {
        if (value != null && !value.isBlank()) {
            claims.put(claimName, value);
        }
    }

    private UUID extractUuidClaim(String token, String claimName) {
        String value = extractStringClaim(token, claimName);
        if (value == null) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String extractStringClaim(String token, String claimName) {
        try {
            Object raw = extractAllClaims(token).get(claimName);
            if (raw == null) {
                return null;
            }
            String value = raw.toString().trim();
            return value.isEmpty() ? null : value;
        } catch (Exception e) {
            return null;
        }
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
