package com.schoolmanagment.coreservice.attendance.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceDto;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceFilterRequest;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceRequest;
import com.schoolmanagment.coreservice.attendance.entity.Attendance;
import com.schoolmanagment.coreservice.attendance.mapper.AttendanceMapper;
import com.schoolmanagment.coreservice.attendance.repository.AttendanceRepository;
import com.schoolmanagment.coreservice.attendance.specification.AttendanceSpecification;
import com.schoolmanagment.coreservice.penalty.entity.Penalty;
import com.schoolmanagment.coreservice.penalty.entity.PenaltyRule;
import com.schoolmanagment.coreservice.attendance.entity.PenaltySourceAttendance;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import com.schoolmanagment.coreservice.penalty.repository.PenaltyRepository;
import com.schoolmanagment.coreservice.penalty.repository.PenaltyRuleRepository;
import com.schoolmanagment.coreservice.attendance.repository.PenaltySourceAttendanceRepository;
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
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceMapper attendanceMapper;
    private final PenaltyRuleRepository penaltyRuleRepository;
    private final PenaltyRepository penaltyRepository;
    private final PenaltySourceAttendanceRepository sourceRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceDto> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return attendanceRepository.findByActiveTrue(pageable)
                .map(attendanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceDto> filter(AttendanceFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return attendanceRepository.findAll(new AttendanceSpecification(request), pageable)
                .map(attendanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDto getById(UUID id) {
        return attendanceMapper.toDto(findActiveAttendanceById(id));
    }

    @Override
    @Transactional
    public AttendanceDto create(AttendanceRequest request) {
        Enrollment enrollment = resolveActiveEnrollment(request.getEnrollmentId());
        validateAttendanceRequest(request, enrollment);
        Attendance attendance = attendanceMapper.toEntity(request, enrollment);
        reconcile(
                attendance.getEnrollment().getId(),
                attendance.getPenaltyTrigger());
        return attendanceMapper.toDto(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional
    public AttendanceDto update(UUID id, AttendanceRequest request) {
        Attendance attendance = findActiveAttendanceById(id);
        Enrollment enrollment = resolveActiveEnrollment(request.getEnrollmentId());
        attendanceMapper.updateEntity(attendance, request, enrollment);
        reconcile(
                attendance.getEnrollment().getId(),
                attendance.getPenaltyTrigger());
        return attendanceMapper.toDto(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Attendance attendance = findActiveAttendanceById(id);
        attendance.setActive(false);
        attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public AttendanceDto deactivate(UUID attendanceId) {
        Attendance attendance = findActiveAttendanceById(attendanceId);
        attendance.setActive(false);
        attendanceRepository.save(attendance);
        reconcile(
                attendance.getEnrollment().getId(),
                attendance.getPenaltyTrigger());
        return attendanceMapper.toDto(attendance);
    }

    private Attendance findActiveAttendanceById(UUID id) {
        return attendanceRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));
    }

    private Enrollment resolveActiveEnrollment(UUID enrollmentId) {
        return enrollmentRepository.findByIdAndActiveTrue(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
    }

    private void validateAttendanceRequest(AttendanceRequest request, Enrollment enrollment) {
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new BadRequestException("Cannot record attendance for a terminated enrollment");
        }

        PenaltyTrigger penaltyTrigger = request.getPenaltyTrigger();
        if (penaltyTrigger == null || penaltyTrigger.getSourceModule() != SourceModule.ATTENDANCE) {
            throw new BadRequestException("Penalty trigger must belong to the ATTENDANCE module");
        }
    }

    @Transactional
    public void reconcile(UUID enrollmentId, PenaltyTrigger trigger) {

        List<Attendance> activeRecords =
                attendanceRepository.findActiveByEnrollmentAndTrigger(enrollmentId, trigger);

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

                List<PenaltySourceAttendance> links = activeRecords.stream()
                        .map(a -> PenaltySourceAttendance.builder()
                                .penalty(penalty)
                                .attendance(a)
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
