package com.schoolmanagment.coreservice.penalty.dto;

import com.schoolmanagment.coreservice.penalty.entity.PenaltyRule;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyType;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
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
public class PenaltyRuleDto {

    private UUID id;
    private UUID schoolId;
    private PenaltyTrigger penaltyTrigger;
    private SourceModule sourceModule;
    private PenaltyType penaltyType;
    private Integer occurrenceNumber;
    private LocalDateTime createdAt;
    private String createdByName;
    private String updatedByName;

    public static PenaltyRuleDto fromEntity(PenaltyRule penaltyRule) {
        PenaltyTrigger penaltyTrigger = penaltyRule.getPenaltyTrigger();
        return PenaltyRuleDto.builder()
                .id(penaltyRule.getId())
                .schoolId(penaltyRule.getSchoolId())
                .penaltyTrigger(penaltyTrigger)
                .sourceModule(penaltyTrigger != null ? penaltyTrigger.getSourceModule() : null)
                .penaltyType(penaltyRule.getPenaltyType())
                .occurrenceNumber(penaltyRule.getOccurrenceNumber())
                .createdAt(penaltyRule.getCreatedAt())
                .createdByName(penaltyRule.getCreatedByName())
                .updatedByName(penaltyRule.getUpdatedByName())
                .build();
    }
}
