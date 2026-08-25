package com.schoolmanagment.userservice.policy.dto;

import com.schoolmanagment.userservice.policy.entity.Policy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicySummaryDto {
    private UUID id;
    private String name;
    private String description;
    private Policy.PolicyEffect effect;
    private LocalDateTime createdAt;
}
