package com.schoolmanagment.commonsecurity.security;


import com.schoolmanagment.commonsecurity.checker.ScopeSatisfaction;
import com.schoolmanagment.commonsecurity.client.UserRbacClient;
import com.schoolmanagment.commonapplication.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * One lazy fetch of granted scopes per HTTP request.
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class GrantedScopesCache {

    private final UserRbacClient userRbacClient;

    private Map<String, List<String>> cache;

    public Map<String, List<String>> getScopesByResource() {
        if (cache != null) {
            return cache;
        }
        String auth = resolveAuthorizationHeader();
        if (auth == null || !auth.startsWith("Bearer ")) {
            cache = Collections.emptyMap();
            return cache;
        }
        try {
            ApiResponse<Map<String, List<String>>> response = userRbacClient.getGrantedScopes(auth);
            if (response != null && response.isSuccess() && response.getData() != null) {
                cache = response.getData();
            } else {
                cache = Collections.emptyMap();
            }
        } catch (RuntimeException ex) {
            cache = Collections.emptyMap();
        }
        return cache;
    }

    public boolean hasScope(String resource, String scope) {
        for (Map.Entry<String, List<String>> e : getScopesByResource().entrySet()) {
            if (e.getKey() == null || !e.getKey().equalsIgnoreCase(resource.trim())) {
                continue;
            }
            return ScopeSatisfaction.satisfiedBy(e.getValue(), scope);
        }
        return false;
    }

    private static String resolveAuthorizationHeader() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            HttpServletRequest request = servletAttrs.getRequest();
            return request.getHeader("Authorization");
        }
        return null;
    }
}
