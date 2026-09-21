package com.schoolmanagment.coreservice.offencerecord.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordDto;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordFilterRequest;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordRequest;
import com.schoolmanagment.coreservice.offencerecord.entity.OffenceRecord;
import com.schoolmanagment.coreservice.offencerecord.mapper.OffenceRecordMapper;
import com.schoolmanagment.coreservice.offencerecord.repository.OffenceRecordRepository;
import com.schoolmanagment.coreservice.offencerecord.specification.OffenceRecordSpecification;
import com.schoolmanagment.coreservice.penalty.entity.Penalty;
import com.schoolmanagment.coreservice.penalty.entity.PenaltyRule;
import com.schoolmanagment.coreservice.offencerecord.entity.PenaltySourceOffenceRecord;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.repository.PenaltyRepository;
import com.schoolmanagment.coreservice.penalty.repository.PenaltyRuleRepository;
import com.schoolmanagment.coreservice.offencerecord.repository.PenaltySourceOffenceRecordRepository;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import com.schoolmanagment.coreservice.student.repository.EnrollmentRepository;
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
    private final EnrollmentRepository enrollmentRepository;
    private final OffenceRecordMapper offenceRecordMapper;
    private final PenaltyRuleRepository penaltyRuleRepository;
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
        Enrollment enrollment = resolveActiveEnrollment(request.getEnrollmentId());
        validateEnrollmentForOffence(enrollment, enrollment.getAcademicYear());
        OffenceRecord offenceRecord = offenceRecordMapper.toEntity(request, enrollment, enrollment.getAcademicYear());
        reconcile(
                offenceRecord.getEnrollment().getId(),
                offenceRecord.getPenaltyTrigger());
        return offenceRecordMapper.toDto(offenceRecordRepository.save(offenceRecord));
    }

    @Override
    @Transactional
    public OffenceRecordDto update(UUID id, OffenceRecordRequest request) {
        OffenceRecord offenceRecord = findActiveOffenceRecordById(id);
        Enrollment enrollment = resolveActiveEnrollment(request.getEnrollmentId());
        validateEnrollmentForOffence(enrollment, enrollment.getAcademicYear());
        offenceRecordMapper.updateEntity(offenceRecord, request, enrollment, enrollment.getAcademicYear());
        reconcile(
                offenceRecord.getEnrollment().getId(),
                offenceRecord.getPenaltyTrigger());
        return offenceRecordMapper.toDto(offenceRecordRepository.save(offenceRecord));
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
                offenceRecord.getEnrollment().getId(),
                offenceRecord.getPenaltyTrigger());
        return offenceRecordMapper.toDto(offenceRecord);
    }

    private OffenceRecord findActiveOffenceRecordById(UUID id) {
        return offenceRecordRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offence record not found with id: " + id));
    }

    private Enrollment resolveActiveEnrollment(UUID enrollmentId) {
        return enrollmentRepository.findByIdAndActiveTrue(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
    }

    private void validateEnrollmentForOffence(Enrollment enrollment, AcademicYear academicYear) {
        AcademicYear enrollmentYear = enrollment.getAcademicYear();
        if (enrollmentYear == null || !academicYear.getId().equals(enrollmentYear.getId())) {
            throw new BadRequestException("Academic year must match the enrollment's academic year");
        }

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new BadRequestException("Cannot record an offence for a terminated enrollment");
        }
    }

    @Transactional
    public void reconcile(UUID enrollmentId, PenaltyTrigger trigger) {

        List<OffenceRecord> activeRecords =
                offenceRecordRepository.findActiveByEnrollmentAndTrigger(enrollmentId, trigger);

        int currentCount = activeRecords.size();

        List<PenaltyRule> rules = penaltyRuleRepository.findActiveByTrigger(trigger);

        for (PenaltyRule rule : rules) {
            Optional<Penalty> existing =
                    penaltyRepository.findActiveByEnrollmentAndRule(enrollmentId, rule.getId());

            boolean shouldExist = currentCount >= rule.getOccurrenceNumber();

            if (shouldExist && existing.isEmpty()) {
                Penalty penalty = Penalty.builder()
                        .penaltyRule(rule)
                        .enrollment(activeRecords.get(0).getEnrollment())
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
