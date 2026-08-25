package com.schoolmanagment.userservice.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class PermissionRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    private String description;

    @NotNull(message = "Resource id is required")
    private UUID resourceId;

    private Set<UUID> scopeIds;

    private Set<UUID> policyIds;
}
