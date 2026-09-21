package com.schoolmanagment.coreservice.penalty.dto;

import com.schoolmanagment.coreservice.penalty.entity.Penalty;
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
public class PenaltyDto {

    private UUID id;
    private UUID schoolId;
    private PenaltyTrigger penaltyTrigger;
    private SourceModule sourceModule;
    private PenaltyType penaltyType;
    private Integer occurrenceNumber;
    private LocalDateTime createdAt;
    private String createdByName;
    private String updatedByName;

    public static PenaltyDto fromEntity(Penalty penalty) {
        PenaltyTrigger penaltyTrigger = penalty.getPenaltyTrigger();
        return PenaltyDto.builder()
                .id(penalty.getId())
                .schoolId(penalty.getSchoolId())
                .penaltyTrigger(penaltyTrigger)
                .sourceModule(penaltyTrigger != null ? penaltyTrigger.getSourceModule() : null)
                .penaltyType(penalty.getPenaltyType())
                .occurrenceNumber(penalty.getOccurrenceNumber())
                .createdAt(penalty.getCreatedAt())
                .createdByName(penalty.getCreatedByName())
                .updatedByName(penalty.getUpdatedByName())
                .build();
    }
}
