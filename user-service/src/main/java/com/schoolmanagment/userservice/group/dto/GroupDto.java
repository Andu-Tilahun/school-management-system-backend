package com.schoolmanagment.userservice.group.dto;

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
public class GroupDto {
    private UUID id;
    private String name;
    private String description;
    private Set<PolicySummaryDto> policies;
    private LocalDateTime createdAt;
}
