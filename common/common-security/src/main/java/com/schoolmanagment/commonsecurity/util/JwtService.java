package com.schoolmanagment.commonsecurity.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtService {

    public static final String CLAIM_USER_SCOPE_TYPE = "userScopeType";
    public static final String CLAIM_EXTERNAL_ID = "externalId";

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

    /**
     * JWT claim key remains {@code roles} for backward compatibility; values are policy authorities
     * ({@code POLICY_&lt;name&gt;}) only.
     */
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        List<String> roles = claims.get("roles", List.class);
        return roles != null ? roles : Collections.emptyList();
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        return extractRoles(token).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
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

    public String generateToken(String userId, Collection<String> effectivePolicyNames) {
        return generateToken(userId, effectivePolicyNames, null);
    }

    /**
     * @param externalId region or organization id for scoped representatives; omitted from token when null
     */
    public String generateToken(String userId, Collection<String> effectivePolicyNames, UUID externalId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", buildPolicyAuthorities(effectivePolicyNames));
        if (externalId != null) {
            claims.put(CLAIM_EXTERNAL_ID, externalId.toString());
        }
        return createToken(claims, userId, expiration);
    }

    public String generateRefreshToken(String userId, Collection<String> effectivePolicyNames) {
        return generateRefreshToken(userId, effectivePolicyNames, null);
    }

    public String generateRefreshToken(String userId, Collection<String> effectivePolicyNames, UUID externalId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", buildPolicyAuthorities(effectivePolicyNames));
        claims.put("tokenType", "refresh");
        if (externalId != null) {
            claims.put(CLAIM_EXTERNAL_ID, externalId.toString());
        }
        return createToken(claims, userId, refreshExpiration);
    }

    /**
     * Reads {@link #CLAIM_EXTERNAL_ID} from a valid JWT body; returns null if absent or invalid.
     */
    public UUID extractExternalId(String token) {
        try {
            Object raw = extractAllClaims(token).get(CLAIM_EXTERNAL_ID);
            if (raw == null) {
                return null;
            }
            String s = raw.toString().trim();
            if (s.isEmpty()) {
                return null;
            }
            return UUID.fromString(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<String> buildPolicyAuthorities(Collection<String> effectivePolicyNames) {
        List<String> authorities = new ArrayList<>();
        if (effectivePolicyNames != null) {
            for (String name : effectivePolicyNames) {
                if (name != null && !name.isBlank()) {
                    authorities.add("POLICY_" + name);
                }
            }
        }
        return authorities;
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
