package com.schoolmanagment.coreservice.offencerecord.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordDto;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordFilterRequest;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordRequest;
import com.schoolmanagment.coreservice.offencerecord.entity.OffenceRecord;
import com.schoolmanagment.coreservice.offencerecord.entity.PenaltySourceOffenceRecord;
import com.schoolmanagment.coreservice.offencerecord.mapper.OffenceRecordMapper;
import com.schoolmanagment.coreservice.offencerecord.repository.OffenceRecordRepository;
import com.schoolmanagment.coreservice.offencerecord.repository.PenaltySourceOffenceRecordRepository;
import com.schoolmanagment.coreservice.offencerecord.specification.OffenceRecordSpecification;
import com.schoolmanagment.coreservice.penalty.entity.Penalty;
import com.schoolmanagment.coreservice.penalty.entity.PenaltyRule;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.repository.PenaltyRepository;
import com.schoolmanagment.coreservice.penalty.service.PenaltyRuleService;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import com.schoolmanagment.coreservice.student.service.EnrollmentService;
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
public class OffenceRecordServiceImpl implements OffenceRecordService {

    private final OffenceRecordRepository offenceRecordRepository;
    private final EnrollmentService enrollmentService;
    private final OffenceRecordMapper offenceRecordMapper;
    private final PenaltyRuleService penaltyRuleService;
    private final PenaltyRepository penaltyRepository;
    private final PenaltySourceOffenceRecordRepository sourceRepository;


    @Override
    @Transactional(readOnly = true)
    public Page<OffenceRecordDto> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return offenceRecordRepository.findByActiveTrue(pageable)
                .map(offenceRecordMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OffenceRecordDto> filter(OffenceRecordFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return offenceRecordRepository.findAll(new OffenceRecordSpecification(request), pageable)
                .map(offenceRecordMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OffenceRecordDto getById(UUID id) {
        return offenceRecordMapper.toDto(findActiveOffenceRecordById(id));
    }

    @Override
    @Transactional
    public OffenceRecordDto create(OffenceRecordRequest request) {
        EnrollmentTerm enrollmentTerm = resolveActiveEnrollmentTerm(request.getStudentId());
        validateEnrollmentForOffence(enrollmentTerm);
        OffenceRecord offenceRecord = offenceRecordMapper.toEntity(request, enrollmentTerm);
        OffenceRecord saved = offenceRecordRepository.save(offenceRecord);
        reconcile(
                saved.getEnrollmentTerm().getEnrollment().getId(),
                saved.getPenaltyTrigger());
        return offenceRecordMapper.toDto(saved);
    }

    @Override
    @Transactional
    public OffenceRecordDto update(UUID id, OffenceRecordRequest request) {
        OffenceRecord offenceRecord = findActiveOffenceRecordById(id);
        UUID previousEnrollmentId = offenceRecord.getEnrollmentTerm().getEnrollment().getId();
        PenaltyTrigger previousTrigger = offenceRecord.getPenaltyTrigger();
        EnrollmentTerm enrollmentTerm = resolveActiveEnrollmentTerm(request.getStudentId());
        offenceRecordMapper.updateEntity(offenceRecord, request, enrollmentTerm);
        OffenceRecord saved = offenceRecordRepository.save(offenceRecord);
        UUID enrollmentId = saved.getEnrollmentTerm().getEnrollment().getId();
        if (!previousEnrollmentId.equals(enrollmentId) || previousTrigger != saved.getPenaltyTrigger()) {
            reconcile(previousEnrollmentId, previousTrigger);
        }
        reconcile(enrollmentId, saved.getPenaltyTrigger());
        return offenceRecordMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        OffenceRecord offenceRecord = findActiveOffenceRecordById(id);
        offenceRecord.setActive(false);
        offenceRecordRepository.save(offenceRecord);
    }

    @Override
    @Transactional
    public OffenceRecordDto deactivate(UUID offenceRecordId) {
        OffenceRecord offenceRecord = findActiveOffenceRecordById(offenceRecordId);
        offenceRecord.setActive(false);
        offenceRecordRepository.save(offenceRecord);
        reconcile(
                offenceRecord.getEnrollmentTerm().getEnrollment().getId(),
                offenceRecord.getPenaltyTrigger());
        return offenceRecordMapper.toDto(offenceRecord);
    }

    private OffenceRecord findActiveOffenceRecordById(UUID id) {
        return offenceRecordRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offence record not found with id: " + id));
    }

    private EnrollmentTerm resolveActiveEnrollmentTerm(UUID studentId) {
        List<EnrollmentTerm> enrollmentTerms = enrollmentService
                .findActiveEnrollmentTermsByStudentIdAndSchoolId(studentId, currentSchoolId());
        if (enrollmentTerms.isEmpty()) {
            throw new ResourceNotFoundException("Active enrollment term not found for student: " + studentId);
        }
        if (enrollmentTerms.size() > 1) {
            throw new BadRequestException("Student has more than one active enrollment term: " + studentId);
        }
        return enrollmentTerms.get(0);
    }

    private UUID currentSchoolId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No schoolId on the current authentication — cannot save a school-scoped entity without one."));
    }

    private void validateEnrollmentForOffence(EnrollmentTerm enrollmentTerm) {
        Enrollment enrollment = enrollmentTerm.getEnrollment();
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new BadRequestException("Cannot record an offence for a terminated enrollment");
        }
        if (enrollmentTerm.getStatus() != EnrollmentTermStatus.ACTIVE) {
            throw new BadRequestException("Cannot record an offence for a non-active enrollment term");
        }
    }

    @Transactional
    public void reconcile(UUID enrollmentId, PenaltyTrigger trigger) {

        List<OffenceRecord> activeRecords =
                offenceRecordRepository.findActiveByEnrollmentAndTrigger(enrollmentId, trigger);

        int currentCount = activeRecords.size();

        List<PenaltyRule> rules = penaltyRuleService.findActiveByTrigger(trigger);

        for (PenaltyRule rule : rules) {
            Optional<Penalty> existing =
                    penaltyRepository.findActiveByEnrollmentAndRule(enrollmentId, rule.getId());

            boolean shouldExist = currentCount >= rule.getOccurrenceNumber();

            if (shouldExist && existing.isEmpty()) {
                Penalty penalty = Penalty.builder()
                        .penaltyRule(rule)
                        .enrollment(activeRecords.get(0).getEnrollmentTerm().getEnrollment())
                        .occurrenceCountAtTrigger(currentCount)
                        .build();
                penaltyRepository.save(penalty);

                List<PenaltySourceOffenceRecord> links = activeRecords.stream()
                        .map(a -> PenaltySourceOffenceRecord.builder()
                                .penalty(penalty)
                                .offenceRecord(a)
                                .build())
                        .toList();
                sourceRepository.saveAll(links);

            } else if (!shouldExist && existing.isPresent()) {
                Penalty penalty = existing.get();
                penalty.setActive(false);
                penaltyRepository.save(penalty);
            }
        }
    }
}
