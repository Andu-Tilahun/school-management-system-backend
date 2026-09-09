package com.schoolmanagment.userservice.user.dto;

import com.schoolmanagment.userservice.group.dto.GroupSummaryDto;
import com.schoolmanagment.userservice.policy.dto.PolicySummaryDto;
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
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private UUID profileImageUuid;
    private String profileUrl;
    private Boolean enabled;
    private Boolean accountNonLocked;
    private Set<GroupSummaryDto> groups;
    private Set<PolicySummaryDto> directPolicies;
    private Set<UUID> effectivePolicyIds;
    private Set<String> effectivePolicyNames;
    private UUID externalId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
