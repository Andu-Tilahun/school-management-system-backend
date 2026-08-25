package com.schoolmanagment.userservice.security;

import com.schoolmanagment.commonsecurity.checker.PermissionEvaluator;
import com.schoolmanagment.userservice.menu.service.NavigationMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RbacPermissionEvaluator implements PermissionEvaluator {

    private final NavigationMenuService navigationMenuService;

    @Override
    public boolean hasPermission(UUID userId, String resource, String scope) {
        return navigationMenuService.userHasScope(userId, resource, scope);
    }
}
