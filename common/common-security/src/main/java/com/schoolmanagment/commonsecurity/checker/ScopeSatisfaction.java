package com.schoolmanagment.commonsecurity.checker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * RBAC scope matching: {@code READ} is satisfied by {@code READ} or {@code VIEW};
 * {@code VIEW} is satisfied only by {@code VIEW} (sidebar/menu tier). Other scopes match exactly.
 */
public final class ScopeSatisfaction {

    private ScopeSatisfaction() {
    }

    public static boolean satisfiedBy(Collection<String> grantedScopesForResource, String requiredScope) {
        if (requiredScope == null || requiredScope.isBlank()) {
            return false;
        }
        String req = requiredScope.trim().toUpperCase(Locale.ROOT);
        Set<String> g = normalizeGrants(grantedScopesForResource);
        if (g.isEmpty()) {
            return false;
        }
        if ("READ".equals(req)) {
            return g.contains("READ") || g.contains("VIEW");
        }
        if ("VIEW".equals(req)) {
            return g.contains("VIEW");
        }
        return g.contains(req);
    }

    private static Set<String> normalizeGrants(Collection<String> grantedScopesForResource) {
        if (grantedScopesForResource == null || grantedScopesForResource.isEmpty()) {
            return Set.of();
        }
        Set<String> g = new HashSet<>();
        for (String s : grantedScopesForResource) {
            if (s != null && !s.isBlank()) {
                g.add(s.trim().toUpperCase(Locale.ROOT));
            }
        }
        return g;
    }
}
