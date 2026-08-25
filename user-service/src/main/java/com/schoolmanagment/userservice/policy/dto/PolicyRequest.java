package com.schoolmanagment.userservice.policy.dto;

import com.schoolmanagment.userservice.policy.entity.Policy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class PolicyRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    private String description;

    @NotNull(message = "Effect is required")
    private Policy.PolicyEffect effect;

    private Set<UUID> permissionIds;
}
