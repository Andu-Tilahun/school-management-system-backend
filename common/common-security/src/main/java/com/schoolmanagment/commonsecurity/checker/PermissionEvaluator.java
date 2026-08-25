package com.schoolmanagment.commonsecurity.checker;

import java.util.UUID;

/**
 * Service-local RBAC check: whether the user has the given resource scope (e.g. USERS + VIEW).
 * Implement in each application that uses {@link RequiresPermission} (typically backed by DB or JWT claims).
 */
@FunctionalInterface
public interface PermissionEvaluator {

    boolean hasPermission(UUID userId, String resource, String scope);
}
