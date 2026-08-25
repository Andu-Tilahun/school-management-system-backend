package com.schoolmanagment.commonsecurity.security;

import com.schoolmanagment.commonsecurity.checker.PermissionEvaluator;
import com.schoolmanagment.commonsecurity.config.RemoteRbacPermissionConfiguration;

import java.util.UUID;

/**
 * Delegates to user-service RBAC via {@link GrantedScopesCache} (forwarded JWT).
 * Registered as a bean only when no other {@link PermissionEvaluator} is present
 * (see {@link RemoteRbacPermissionConfiguration}).
 */
public class RemoteRbacPermissionEvaluator implements PermissionEvaluator {

    private final GrantedScopesCache grantedScopesCache;

    public RemoteRbacPermissionEvaluator(GrantedScopesCache grantedScopesCache) {
        this.grantedScopesCache = grantedScopesCache;
    }

    @Override
    public boolean hasPermission(UUID userId, String resource, String scope) {
        return grantedScopesCache.hasScope(resource, scope);
    }
}
