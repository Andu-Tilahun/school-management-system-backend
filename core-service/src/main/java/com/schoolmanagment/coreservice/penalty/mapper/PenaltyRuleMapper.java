package com.schoolmanagment.coreservice.penalty.mapper;

import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleDto;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleRequest;
import com.schoolmanagment.coreservice.penalty.entity.PenaltyRule;
import org.springframework.stereotype.Component;

@Component
public class PenaltyRuleMapper {

    public PenaltyRuleDto toDto(PenaltyRule penaltyRule) {
        return PenaltyRuleDto.fromEntity(penaltyRule);
    }

    public PenaltyRule toEntity(PenaltyRuleRequest request) {
        return PenaltyRule.builder()
                .penaltyTrigger(request.getPenaltyTrigger())
                .penaltyType(request.getPenaltyType())
                .occurrenceNumber(request.getOccurrenceNumber())
                .active(true)
                .build();
    }

    public void updateEntity(PenaltyRule penaltyRule, PenaltyRuleRequest request) {
        penaltyRule.setPenaltyTrigger(request.getPenaltyTrigger());
        penaltyRule.setPenaltyType(request.getPenaltyType());
        penaltyRule.setOccurrenceNumber(request.getOccurrenceNumber());
    }
}
