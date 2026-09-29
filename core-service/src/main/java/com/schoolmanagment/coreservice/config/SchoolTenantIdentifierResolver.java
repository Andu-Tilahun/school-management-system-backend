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

        if (isTenantLevel(context)) {
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
        return isTenantLevel(UserContext.current());
    }


    private static boolean isTenantLevel(UserContext context) {
        if (context == null) {
            throw new IllegalStateException("No user context set for this request");
        }
        return context.hasAdminPolicy() || context.hasTenantManager();
    }
}