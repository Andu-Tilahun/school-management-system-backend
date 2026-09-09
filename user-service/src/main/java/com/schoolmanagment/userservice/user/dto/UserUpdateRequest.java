package com.schoolmanagment.userservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String middleName;

    @NotBlank(message = "Gender is required")
    private String gender;

    private UUID profileImageUuid;

    private Set<UUID> groupIds;
    private Set<UUID> policyIds;

    private UUID externalId;
}
