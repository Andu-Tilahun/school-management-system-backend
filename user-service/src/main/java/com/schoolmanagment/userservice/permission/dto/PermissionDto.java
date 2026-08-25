package com.schoolmanagment.userservice.permission.dto;

import com.schoolmanagment.userservice.policy.dto.PolicySummaryDto;
import com.schoolmanagment.userservice.resource.dto.ResourceDto;
import com.schoolmanagment.userservice.scope.dto.ScopeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {
    private UUID id;
    private String name;
    private String description;
    private ResourceDto resource;
    private Set<ScopeDto> scopes;
    private Set<PolicySummaryDto> policies;
    private LocalDateTime createdAt;
}
