package com.schoolmanagment.userservice.filter;


import com.schoolmanagment.commonsecurity.auth.JwtAuthDetails;
import com.schoolmanagment.commonsecurity.util.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final SecurityProperties securityProperties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private boolean isPublicEndpoint(String path, String method) {
        return securityProperties.getPublicEndpoints().stream()
                .anyMatch(endpoint -> {
                    // Convert {variable} to AntMatcher wildcard *
                    String pattern = endpoint.getPath().replaceAll("\\{[^/]+}", "*");

                    boolean pathMatches = pathMatcher.match(pattern, path);
                    boolean methodMatches = endpoint.getMethod() == null
                            || endpoint.getMethod().equals("*")
                            || endpoint.getMethod().equalsIgnoreCase(method);
                    return pathMatches && methodMatches;
                });
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String path = request.getServletPath();
        String method = request.getMethod();

        if (isPublicEndpoint(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Require valid Authorization header for protected endpoints
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userId = jwtService.extractUserId(jwt);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.validateToken(jwt)) {
                List authorities = jwtService.extractAuthorities(jwt);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        authorities
                );
                UUID externalId = jwtService.extractExternalId(jwt);
                authToken.setDetails(new JwtAuthDetails(externalId));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
