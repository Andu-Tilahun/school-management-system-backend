package com.schoolmanagment.coreservice.attendance.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.helper.AcademicYearHelper;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceDto;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceFilterRequest;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceRequest;
import com.schoolmanagment.coreservice.attendance.entity.Attendance;
import com.schoolmanagment.coreservice.attendance.mapper.AttendanceMapper;
import com.schoolmanagment.coreservice.attendance.repository.AttendanceRepository;
import com.schoolmanagment.coreservice.attendance.specification.AttendanceSpecification;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import com.schoolmanagment.coreservice.student.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceMapper attendanceMapper;

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
        UUID schoolId = currentSchoolId();
        Enrollment enrollment = resolveActiveEnrollment(request.getEnrollmentId());
        AcademicYear academicYear = AcademicYearHelper.getActiveAcademicYear();
        validateAttendanceRequest(request, enrollment, academicYear, schoolId);

        Attendance attendance = attendanceMapper.toEntity(request, enrollment, academicYear);
        attendance.setSchoolId(schoolId);
        return attendanceMapper.toDto(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional
    public AttendanceDto update(UUID id, AttendanceRequest request) {
        Attendance attendance = findActiveAttendanceById(id);
        Enrollment enrollment = resolveActiveEnrollment(request.getEnrollmentId());
        AcademicYear academicYear = AcademicYearHelper.getActiveAcademicYear();
        validateAttendanceRequest(request, enrollment, academicYear, attendance.getSchoolId());
        attendanceMapper.updateEntity(attendance, request, enrollment, academicYear);
        return attendanceMapper.toDto(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Attendance attendance = findActiveAttendanceById(id);
        attendance.setActive(false);
        attendanceRepository.save(attendance);
    }

    private Attendance findActiveAttendanceById(UUID id) {
        return attendanceRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));
    }

    private Enrollment resolveActiveEnrollment(UUID enrollmentId) {
        return enrollmentRepository.findByIdAndActiveTrue(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
    }

    private void validateAttendanceRequest(
            AttendanceRequest request,
            Enrollment enrollment,
            AcademicYear academicYear,
            UUID schoolId
    ) {
        validateBelongsToSchool(enrollment.getSchoolId(), schoolId, "Enrollment");

        AcademicYear enrollmentYear = enrollment.getAcademicYear();
        if (enrollmentYear == null || !academicYear.getId().equals(enrollmentYear.getId())) {
            throw new BadRequestException("Academic year must match the enrollment's academic year");
        }

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new BadRequestException("Cannot record attendance for a terminated enrollment");
        }

        PenaltyTrigger penaltyTrigger = request.getPenaltyTrigger();
        if (penaltyTrigger == null || penaltyTrigger.getSourceModule() != SourceModule.ATTENDANCE) {
            throw new BadRequestException("Penalty trigger must belong to the ATTENDANCE module");
        }
    }

    private void validateBelongsToSchool(UUID entitySchoolId, UUID schoolId, String label) {
        if (!schoolId.equals(entitySchoolId)) {
            throw new BadRequestException(label + " does not belong to this school");
        }
    }

    private UUID currentSchoolId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No schoolId on the current authentication — cannot save a school-scoped entity without one."));
    }
}
