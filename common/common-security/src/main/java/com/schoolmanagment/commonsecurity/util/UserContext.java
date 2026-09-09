package com.schoolmanagment.commonsecurity.util;

import com.schoolmanagment.commonsecurity.PolicyNames;
import com.schoolmanagment.commonsecurity.auth.JwtAuthDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class UserContext {

    private static UserContext instance;

    private final UserStatusCache userStatusCache;

    public UserContext(UserStatusCache userStatusCache) {
        this.userStatusCache = userStatusCache;
    }

    @jakarta.annotation.PostConstruct
    private void registerStaticInstance() {
        instance = this;
    }

    public static UserContext current() {
        return instance;
    }

    public UUID getCurrentUserId() {
        return UUID.fromString(pullAuthentication().getName());
    }

    public boolean isAuthenticated() {
        return pullAuthenticationOptional() != null;
    }

    public boolean hasPolicy(String policyName) {
        if (policyName == null) {
            return false;
        }
        return getEffectivePolicyNames().stream()
                .anyMatch(name -> name.equalsIgnoreCase(policyName));
    }

    private Set<String> getEffectivePolicyNames() {
        Authentication authentication = pullAuthenticationOptional();
        if (authentication == null || authentication.getName() == null) {
            return Collections.emptySet();
        }
        return userStatusCache.getUserPolicies(authentication.getName());
    }

    public Set<String> getUserAuthorities() {
        if (pullAuthenticationOptional() == null) {
            return null;
        }
        return getEffectivePolicyNames().stream()
                .map(name -> name.regionMatches(true, 0, "POLICY_", 0, 7) ? name : "POLICY_" + name)
                .collect(Collectors.toSet());
    }

    public boolean isAdmin() {
        return hasPolicy(PolicyNames.ADMIN_POLICY);
    }

    public Optional<UUID> getCurrentExternalId() {
        Authentication auth = pullAuthenticationOptional();
        if (auth == null || auth.getDetails() == null) {
            return Optional.empty();
        }
        if (auth.getDetails() instanceof JwtAuthDetails details) {
            return Optional.ofNullable(details.externalId());
        }
        return Optional.empty();
    }

    private Authentication pullAuthenticationOptional() {
        try {
            return Optional.ofNullable(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .filter(Authentication::isAuthenticated)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private Authentication pullAuthentication() {
        try {
            return Optional.ofNullable(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .filter(Authentication::isAuthenticated)
                    .orElseThrow(() -> new RuntimeException("No authenticated user found"));

        } catch (Exception e) {
//            throw new ("Unable to retrieve authentication");
        }
        return null;
    }
}
