package com.schoolmanagment.commonsecurity;

/**
 * Canonical policy names stored in {@code policies.name} and carried on the JWT as
 * {@code POLICY_} prefixed authorities (for example {@code POLICY_ADMIN_ALL_FEATURES}).
 */
public final class PolicyNames {

    private PolicyNames() {
    }

    /** Matches seeded policy {@code ADMIN_ALL_FEATURES} in user-service migrations. */
    public static final String ADMIN_POLICY = "ADMIN_ALL_FEATURES";
    public static final String INVESTOR_POLICY = "INVESTOR_POLICY";
    public static final String EXTENSION_WORKER_ASSIGNED = "EXTENSION_WORKER_ASSIGNED";
    public static final String OPERATOR_POLICY = "OPERATOR_POLICY";
    /** Matches {@code users.user_scope_type} / RBAC when region-scoped representatives carry this policy on the JWT. */
    public static final String REGION_REPRESENTATIVE_POLICY = "REGION_REPRESENTATIVE";
    /** Matches {@code users.user_scope_type} / RBAC when organization-scoped representatives carry this policy on the JWT. */
    public static final String ORGANIZATION_REPRESENTATIVE_POLICY = "ORGANIZATION_REPRESENTATIVE";
}
