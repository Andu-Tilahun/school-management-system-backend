package com.schoolmanagment.userservice.permission.mapper;

import com.schoolmanagment.userservice.permission.dto.PermissionDto;
import com.schoolmanagment.userservice.permission.dto.PermissionRequest;
import com.schoolmanagment.userservice.permission.entity.Permission;
import com.schoolmanagment.userservice.policy.mapper.PolicyMapper;
import com.schoolmanagment.userservice.resource.entity.Resource;
import com.schoolmanagment.userservice.resource.mapper.ResourceMapper;
import com.schoolmanagment.userservice.scope.entity.Scope;
import com.schoolmanagment.userservice.scope.mapper.ScopeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionMapper {

    private final ResourceMapper resourceMapper;
    private final ScopeMapper scopeMapper;
    private final PolicyMapper policyMapper;

    public PermissionDto toDto(Permission permission) {
        if (permission == null) {
            return null;
        }
        return PermissionDto.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .resource(resourceMapper.toDto(permission.getResource()))
                .scopes(permission.getScopes() == null
                        ? Collections.emptySet()
                        : permission.getScopes().stream()
                                .map(scopeMapper::toDto)
                                .collect(Collectors.toSet()))
                .policies(permission.getPolicies() == null
                        ? Collections.emptySet()
                        : permission.getPolicies().stream()
                                .map(policyMapper::toSummaryDto)
                                .collect(Collectors.toSet()))
                .createdAt(permission.getCreatedAt())
                .build();
    }

    public Permission toEntity(PermissionRequest request, Resource resource, Set<Scope> scopes) {
        return Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .resource(resource)
                .scopes(scopes != null ? new HashSet<>(scopes) : new HashSet<>())
                .build();
    }

    public void updateEntity(Permission permission, PermissionRequest request, Resource resource, Set<Scope> scopes) {
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission.setResource(resource);
        permission.setScopes(scopes != null ? new HashSet<>(scopes) : new HashSet<>());
    }
}
