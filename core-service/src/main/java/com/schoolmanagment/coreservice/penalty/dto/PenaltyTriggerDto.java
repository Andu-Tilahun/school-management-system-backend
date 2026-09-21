package com.schoolmanagment.coreservice.penalty.dto;

import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyTriggerDto {

    private PenaltyTrigger penaltyTrigger;
    private SourceModule sourceModule;

    public static PenaltyTriggerDto fromEnum(PenaltyTrigger penaltyTrigger) {
        return PenaltyTriggerDto.builder()
                .penaltyTrigger(penaltyTrigger)
                .sourceModule(penaltyTrigger.getSourceModule())
                .build();
    }
}
