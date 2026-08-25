package com.schoolmanagment.commonsecurity.util;

import com.example.commonsecurity.PolicyNames;
import com.schoolmanagment.commonsecurity.auth.JwtAuthDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    public UUID getCurrentUserId() {
        return UUID.fromString(pullAuthentication().getName());
    }

    /**
     * True if the user has the named policy (directly or via group), encoded as a
     * {@code POLICY_&lt;name&gt;} granted authority on the JWT.
     */
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

    /**
     * Policy names from JWT authorities ({@code POLICY_&lt;name&gt;}), without the prefix.
     * In zion these act as transition "roles" (license-service uses {@code roleId} UUIDs instead).
     */
    public Set<String> getEffectivePolicyNames() {
        Authentication authentication = pullAuthenticationOptional();
        if (authentication == null || authentication.getAuthorities() == null) {
            return Collections.emptySet();
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(UserContext::stripPolicyPrefix)
                .collect(Collectors.toSet());
    }

    public Set<String> getUserAuthorities() {
        Authentication authentication = pullAuthenticationOptional();
        if (authentication == null || authentication.getAuthorities() == null) {
            return null;
        }
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return authorities;
    }

    public boolean isAdmin() {
        return hasPolicy(PolicyNames.ADMIN_POLICY);
    }

    public boolean isRegionalRepresentative() {
        return hasPolicy(PolicyNames.REGION_REPRESENTATIVE_POLICY);
    }

    public boolean isOrganizationalRepresentative() {
        return hasPolicy(PolicyNames.ORGANIZATION_REPRESENTATIVE_POLICY);
    }

    public boolean isInvestor() {
        return hasPolicy(PolicyNames.INVESTOR_POLICY);
    }

    public boolean isExtensionWorker() {
        return hasPolicy(PolicyNames.EXTENSION_WORKER_ASSIGNED);
    }

    public boolean isOperator() {
        return hasPolicy(PolicyNames.OPERATOR_POLICY);
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

    private static String stripPolicyPrefix(String authority) {
        if (authority != null && authority.regionMatches(true, 0, "POLICY_", 0, 7)) {
            return authority.substring(7);
        }
        return authority;
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
