package com.schoolmanagment.coreservice.penalty.service;

import com.schoolmanagment.coreservice.penalty.dto.PenaltyDto;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyFilterRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyTriggerDto;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface PenaltyService {

    Page<PenaltyDto> getAllPenalties(int page, int size);

    Page<PenaltyDto> filterPenalties(PenaltyFilterRequest request);

    PenaltyDto getPenaltyById(UUID id);

    List<PenaltyTriggerDto> getPenaltyTriggers(SourceModule sourceModule);

    PenaltyDto createPenalty(PenaltyRequest request);

    PenaltyDto updatePenalty(UUID id, PenaltyRequest request);

    void deletePenalty(UUID id);
}
