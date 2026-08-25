package com.schoolmanagment.userservice.user.dto;

import com.schoolmanagment.userservice.user.enums.UserScopeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

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

    private Set<UUID> groupIds;
    private Set<UUID> policyIds;

    private UUID profileImageUuid;

    @NotNull(message = "User type is required")
    private UserScopeType userScopeType;

    /** Region or organization id when {@link #userScopeType} is not {@link UserScopeType#SYSTEM}. */
    private UUID externalId;
}
