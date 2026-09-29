package com.schoolmanagment.coreservice.config;

import com.schoolmanagment.commonsecurity.util.UserContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SchoolTenantIdentifierResolver
        implements CurrentTenantIdentifierResolver<UUID> {

    private static final UUID UNSCOPED = new UUID(0L, 0L);

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        UserContext context = UserContext.current();

        // Hibernate resolves a tenant while the EntityManagerFactory and repositories
        // are created, before any request has a security context.
        if (context == null || !context.isAuthenticated() || isTenantLevel(context)) {
            return UNSCOPED;
        }

        return context.getCurrentExternalId()
                .orElseThrow(() -> new IllegalStateException(
                        "No active school context set for this request"));
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    @Override
    public boolean isRoot(UUID tenantId) {
        return UNSCOPED.equals(tenantId);
    }

    private static boolean isTenantLevel(UserContext context) {
        return context.hasAdminPolicy() || context.hasTenantManager();
    }
}