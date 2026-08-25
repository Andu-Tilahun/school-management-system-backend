package com.schoolmanagment.commonsecurity.checker;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

/**
 * Enforces {@link RequiresPermission} using a {@link PermissionEvaluator} bean from the host application.
 */
@Aspect
@Component
@Order(0)
public class PermissionAspect {

    private final ObjectProvider<PermissionEvaluator> permissionEvaluator;

    public PermissionAspect(ObjectProvider<PermissionEvaluator> permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @Around("@annotation(requiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        PermissionEvaluator evaluator = permissionEvaluator.getIfAvailable();
        if (evaluator == null) {
            throw new IllegalStateException(
                    "RequiresPermission is used but no PermissionEvaluator bean is registered; "
                            + "provide one in the application context."
            );
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Authentication required");
        }

        UUID userId;
        try {
            userId = UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ex) {
            throw new AccessDeniedException("Invalid principal: expected user id");
        }

        String resource = requiresPermission.resource().trim();
        String scope = requiresPermission.scope().trim().toUpperCase(Locale.ROOT);
        if (resource.isEmpty() || scope.isEmpty()) {
            throw new IllegalStateException("@RequiresPermission resource and scope must be non-blank");
        }

        if (!evaluator.hasPermission(userId, resource, scope)) {
            throw new AccessDeniedException(String.format("Missing permission %s:%s", resource, scope));
        }

        return joinPoint.proceed();
    }
}
