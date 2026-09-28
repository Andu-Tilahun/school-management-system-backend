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
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import com.schoolmanagment.coreservice.student.repository.EmergencyContactRepository;
import com.schoolmanagment.coreservice.student.repository.EnrollmentTermRepository;
import com.schoolmanagment.coreservice.student.repository.StudentEmergencyContactRepository;
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
    private final EnrollmentTermRepository enrollmentTermRepository;
    private final AttendanceMapper attendanceMapper;
    private final PenaltyRuleRepository penaltyRuleRepository;
    private final PenaltyRepository penaltyRepository;
    private final PenaltySourceAttendanceRepository sourceRepository;
    private final StudentEmergencyContactRepository studentEmergencyContactRepository;
    private final EmergencyContactRepository emergencyContactRepository;

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
    public List<StudentDto> getStudentsForCurrentEmergencyContact() {
        UUID emergencyContactId = currentEmergencyContactId();
        return studentEmergencyContactRepository.findActiveStudentsByEmergencyContactId(emergencyContactId).stream()
                .map(StudentDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDto getById(UUID id) {
        return attendanceMapper.toDto(findActiveAttendanceById(id));
    }

    @Override
    @Transactional
    public AttendanceDto create(AttendanceRequest request) {
        EnrollmentTerm enrollmentTerm = resolveActiveEnrollmentTerm(request.getStudentId());
        validateAttendanceRequest(request, enrollmentTerm);
        Attendance attendance = attendanceMapper.toEntity(request, enrollmentTerm);
        Attendance saved = attendanceRepository.save(attendance);
        reconcile(
                saved.getEnrollmentTerm().getEnrollment().getId(),
                saved.getPenaltyTrigger());
        return attendanceMapper.toDto(saved);
    }

    @Override
    @Transactional
    public AttendanceDto update(UUID id, AttendanceRequest request) {
        Attendance attendance = findActiveAttendanceById(id);
        UUID previousEnrollmentId = attendance.getEnrollmentTerm().getEnrollment().getId();
        PenaltyTrigger previousTrigger = attendance.getPenaltyTrigger();
        EnrollmentTerm enrollmentTerm = resolveActiveEnrollmentTerm(request.getStudentId());
        attendanceMapper.updateEntity(attendance, request, enrollmentTerm);
        Attendance saved = attendanceRepository.save(attendance);
        UUID enrollmentId = saved.getEnrollmentTerm().getEnrollment().getId();
        if (!previousEnrollmentId.equals(enrollmentId) || previousTrigger != saved.getPenaltyTrigger()) {
            reconcile(previousEnrollmentId, previousTrigger);
        }
        reconcile(enrollmentId, saved.getPenaltyTrigger());
        return attendanceMapper.toDto(saved);
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
                attendance.getEnrollmentTerm().getEnrollment().getId(),
                attendance.getPenaltyTrigger());
        return attendanceMapper.toDto(attendance);
    }

    private Attendance findActiveAttendanceById(UUID id) {
        return attendanceRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));
    }

    private EnrollmentTerm resolveActiveEnrollmentTerm(UUID studentId) {
        List<EnrollmentTerm> enrollmentTerms = enrollmentTermRepository.findActiveByStudentIdAndSchoolId(
                studentId,
                currentSchoolId(),
                EnrollmentStatus.ACTIVE,
                EnrollmentTermStatus.ACTIVE);
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

    private void validateAttendanceRequest(AttendanceRequest request, EnrollmentTerm enrollmentTerm) {
        Enrollment enrollment = enrollmentTerm.getEnrollment();
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new BadRequestException("Cannot record attendance for a terminated enrollment");
        }
        if (enrollmentTerm.getStatus() != EnrollmentTermStatus.ACTIVE) {
            throw new BadRequestException("Cannot record attendance for a non-active enrollment term");
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
                        .enrollment(activeRecords.get(0).getEnrollmentTerm().getEnrollment())
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

    private UUID currentEmergencyContactId() {
        UUID emergencyContactId = UserContext.current().getCurrentExternalId()
                .orElseThrow(() -> new BadRequestException("Logged-in emergency contact has no external id"));
        EmergencyContact contact = emergencyContactRepository.findById(emergencyContactId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Emergency contact not found with id: " + emergencyContactId));
        if (!Boolean.TRUE.equals(contact.getActive())) {
            throw new BadRequestException("Emergency contact is not active");
        }
        return emergencyContactId;
    }
}
