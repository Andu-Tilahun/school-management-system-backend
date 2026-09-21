package com.schoolmanagment.coreservice.penalty.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyDto;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyFilterRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyTriggerDto;
import com.schoolmanagment.coreservice.penalty.entity.Penalty;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import com.schoolmanagment.coreservice.penalty.mapper.PenaltyMapper;
import com.schoolmanagment.coreservice.penalty.repository.PenaltyRepository;
import com.schoolmanagment.coreservice.penalty.specification.PenaltySpecification;
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
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final PenaltyMapper penaltyMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PenaltyDto> getAllPenalties(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return penaltyRepository.findByActiveTrue(pageable)
                .map(penaltyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PenaltyDto> filterPenalties(PenaltyFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return penaltyRepository.findAll(new PenaltySpecification(request), pageable)
                .map(penaltyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PenaltyDto getPenaltyById(UUID id) {
        return penaltyMapper.toDto(findActivePenaltyById(id));
    }

    @Override
    public List<PenaltyTriggerDto> getPenaltyTriggers(SourceModule sourceModule) {
        return PenaltyTrigger.valuesFor(sourceModule).stream()
                .map(PenaltyTriggerDto::fromEnum)
                .toList();
    }

    @Override
    @Transactional
    public PenaltyDto createPenalty(PenaltyRequest request) {
        validateOccurrenceNotTaken(currentSchoolId(), request.getPenaltyTrigger(), request.getOccurrenceNumber(), null);
        return penaltyMapper.toDto(penaltyRepository.save(penaltyMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public PenaltyDto updatePenalty(UUID id, PenaltyRequest request) {
        Penalty penalty = findActivePenaltyById(id);
        validateOccurrenceNotTaken(penalty.getSchoolId(), request.getPenaltyTrigger(), request.getOccurrenceNumber(), id);
        penaltyMapper.updateEntity(penalty, request);
        return penaltyMapper.toDto(penaltyRepository.save(penalty));
    }

    @Override
    @Transactional
    public void deletePenalty(UUID id) {
        Penalty penalty = findActivePenaltyById(id);
        penalty.setActive(false);
        penaltyRepository.save(penalty);
    }

    private Penalty findActivePenaltyById(UUID id) {
        return penaltyRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Penalty not found with id: " + id));
    }

    private void validateOccurrenceNotTaken(
            UUID schoolId, PenaltyTrigger penaltyTrigger, Integer occurrenceNumber, UUID excludeId) {
        boolean taken = excludeId == null
                ? penaltyRepository.existsBySchoolIdAndPenaltyTriggerAndOccurrenceNumber(
                        schoolId, penaltyTrigger, occurrenceNumber)
                : penaltyRepository.existsBySchoolIdAndPenaltyTriggerAndOccurrenceNumberAndIdNot(
                        schoolId, penaltyTrigger, occurrenceNumber, excludeId);
        if (taken) {
            throw new BadRequestException(
                    "Penalty already exists for this trigger occurrence in this school");
        }
    }

    private UUID currentSchoolId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No schoolId on the current authentication — cannot save a school-scoped entity without one."));
    }
}
