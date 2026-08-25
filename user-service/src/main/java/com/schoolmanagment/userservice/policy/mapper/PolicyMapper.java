package com.schoolmanagment.userservice.policy.mapper;

import com.schoolmanagment.userservice.permission.dto.PermissionSummaryDto;
import com.schoolmanagment.userservice.permission.entity.Permission;
import com.schoolmanagment.userservice.policy.dto.PolicyDto;
import com.schoolmanagment.userservice.policy.dto.PolicyRequest;
import com.schoolmanagment.userservice.policy.dto.PolicySummaryDto;
import com.schoolmanagment.userservice.policy.entity.Policy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PolicyMapper {

    public PolicyDto toDto(Policy policy) {
        if (policy == null) {
            return null;
        }
        return PolicyDto.builder()
                .id(policy.getId())
                .name(policy.getName())
                .description(policy.getDescription())
                .effect(policy.getEffect())
                .permissions(policy.getPermissions() == null
                        ? Collections.emptySet()
                        : policy.getPermissions().stream()
                                .map(this::toPermissionSummaryDto)
                                .collect(Collectors.toSet()))
                .createdAt(policy.getCreatedAt())
                .build();
    }

    public PolicySummaryDto toSummaryDto(Policy policy) {
        if (policy == null) {
            return null;
        }
        return PolicySummaryDto.builder()
                .id(policy.getId())
                .name(policy.getName())
                .description(policy.getDescription())
                .effect(policy.getEffect())
                .createdAt(policy.getCreatedAt())
                .build();
    }

    public PermissionSummaryDto toPermissionSummaryDto(Permission permission) {
        if (permission == null) {
            return null;
        }
        return PermissionSummaryDto.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .build();
    }

    public Policy toEntity(PolicyRequest request, Set<Permission> permissions) {
        return Policy.builder()
                .name(request.getName())
                .description(request.getDescription())
                .effect(request.getEffect())
                .permissions(permissions != null ? new HashSet<>(permissions) : new HashSet<>())
                .build();
    }

    public void updateEntity(Policy policy, PolicyRequest request, Set<Permission> permissions) {
        policy.setName(request.getName());
        policy.setDescription(request.getDescription());
        policy.setEffect(request.getEffect());
        policy.setPermissions(permissions != null ? new HashSet<>(permissions) : new HashSet<>());
    }
}
