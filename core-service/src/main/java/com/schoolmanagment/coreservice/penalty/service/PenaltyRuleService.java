package com.schoolmanagment.coreservice.penalty.service;

import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleDto;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleFilterRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyTriggerDto;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface PenaltyRuleService {

    Page<PenaltyRuleDto> getAllPenaltyRules(int page, int size);

    Page<PenaltyRuleDto> filterPenaltyRules(PenaltyRuleFilterRequest request);

    PenaltyRuleDto getPenaltyRuleById(UUID id);

    List<PenaltyTriggerDto> getPenaltyTriggers(SourceModule sourceModule);

    PenaltyRuleDto createPenaltyRule(PenaltyRuleRequest request);

    PenaltyRuleDto updatePenaltyRule(UUID id, PenaltyRuleRequest request);

    void deletePenaltyRule(UUID id);
}
