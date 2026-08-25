package com.schoolmanagment.userservice.user.enums;

/**
 * Classifies how a user is scoped in the licensing domain.
 * {@link com.schoolmanagment.userservice.user.entity.User#getExternalId()} holds a region id or organization id when applicable.
 */
public enum UserScopeType {
    SYSTEM,
    REGION_REPRESENTATIVE,
    ORGANIZATION_REPRESENTATIVE
}
