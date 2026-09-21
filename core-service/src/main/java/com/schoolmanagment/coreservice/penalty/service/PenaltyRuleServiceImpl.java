package com.schoolmanagment.coreservice.penalty.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleDto;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleFilterRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyTriggerDto;
import com.schoolmanagment.coreservice.penalty.entity.PenaltyRule;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import com.schoolmanagment.coreservice.penalty.mapper.PenaltyRuleMapper;
import com.schoolmanagment.coreservice.penalty.repository.PenaltyRuleRepository;
import com.schoolmanagment.coreservice.penalty.specification.PenaltyRuleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PenaltyRuleServiceImpl implements PenaltyRuleService {

    private final PenaltyRuleRepository penaltyRuleRepository;
    private final PenaltyRuleMapper penaltyRuleMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PenaltyRuleDto> getAllPenaltyRules(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return penaltyRuleRepository.findByActiveTrue(pageable)
                .map(penaltyRuleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PenaltyRuleDto> filterPenaltyRules(PenaltyRuleFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return penaltyRuleRepository.findAll(new PenaltyRuleSpecification(request), pageable)
                .map(penaltyRuleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PenaltyRuleDto getPenaltyRuleById(UUID id) {
        return penaltyRuleMapper.toDto(findActivePenaltyRuleById(id));
    }

    @Override
    public List<PenaltyTriggerDto> getPenaltyTriggers(SourceModule sourceModule) {
        return PenaltyTrigger.valuesFor(sourceModule).stream()
                .map(PenaltyTriggerDto::fromEnum)
                .toList();
    }

    @Override
    @Transactional
    public PenaltyRuleDto createPenaltyRule(PenaltyRuleRequest request) {
        validateOccurrenceNotTaken(currentSchoolId(), request.getPenaltyTrigger(), request.getOccurrenceNumber(), null);
        return penaltyRuleMapper.toDto(penaltyRuleRepository.save(penaltyRuleMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public PenaltyRuleDto updatePenaltyRule(UUID id, PenaltyRuleRequest request) {
        PenaltyRule penaltyRule = findActivePenaltyRuleById(id);
        validateOccurrenceNotTaken(penaltyRule.getSchoolId(), request.getPenaltyTrigger(), request.getOccurrenceNumber(), id);
        penaltyRuleMapper.updateEntity(penaltyRule, request);
        return penaltyRuleMapper.toDto(penaltyRuleRepository.save(penaltyRule));
    }

    @Override
    @Transactional
    public void deletePenaltyRule(UUID id) {
        PenaltyRule penaltyRule = findActivePenaltyRuleById(id);
        penaltyRule.setActive(false);
        penaltyRuleRepository.save(penaltyRule);
    }

    private PenaltyRule findActivePenaltyRuleById(UUID id) {
        return penaltyRuleRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Penalty rule not found with id: " + id));
    }

    private void validateOccurrenceNotTaken(
            UUID schoolId, PenaltyTrigger penaltyTrigger, Integer occurrenceNumber, UUID excludeId) {
        boolean taken = excludeId == null
                ? penaltyRuleRepository.existsBySchoolIdAndPenaltyTriggerAndOccurrenceNumber(
                        schoolId, penaltyTrigger, occurrenceNumber)
                : penaltyRuleRepository.existsBySchoolIdAndPenaltyTriggerAndOccurrenceNumberAndIdNot(
                        schoolId, penaltyTrigger, occurrenceNumber, excludeId);
        if (taken) {
            throw new BadRequestException(
                    "Penalty rule already exists for this trigger occurrence in this school");
        }
    }

    private UUID currentSchoolId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No schoolId on the current authentication — cannot save a school-scoped entity without one."));
    }
}
