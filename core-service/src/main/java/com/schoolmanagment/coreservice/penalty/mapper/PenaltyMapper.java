package com.schoolmanagment.coreservice.penalty.mapper;

import com.schoolmanagment.coreservice.penalty.dto.PenaltyDto;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRequest;
import com.schoolmanagment.coreservice.penalty.entity.Penalty;
import org.springframework.stereotype.Component;

@Component
public class PenaltyMapper {

    public PenaltyDto toDto(Penalty penalty) {
        return PenaltyDto.fromEntity(penalty);
    }

    public Penalty toEntity(PenaltyRequest request) {
        return Penalty.builder()
                .penaltyTrigger(request.getPenaltyTrigger())
                .penaltyType(request.getPenaltyType())
                .occurrenceNumber(request.getOccurrenceNumber())
                .active(true)
                .build();
    }

    public void updateEntity(Penalty penalty, PenaltyRequest request) {
        penalty.setPenaltyTrigger(request.getPenaltyTrigger());
        penalty.setPenaltyType(request.getPenaltyType());
        penalty.setOccurrenceNumber(request.getOccurrenceNumber());
    }
}
