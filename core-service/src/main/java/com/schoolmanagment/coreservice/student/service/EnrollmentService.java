package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.coreservice.student.dto.EnrollmentDto;
import com.schoolmanagment.coreservice.student.dto.EnrollmentFilterRequest;
import com.schoolmanagment.coreservice.student.dto.EnrollmentRequest;
import com.schoolmanagment.coreservice.student.dto.EnrollmentTermDto;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentService {

    Page<EnrollmentDto> listByStudent(UUID studentId, EnrollmentFilterRequest filter);

    EnrollmentDto getById(UUID studentId, UUID enrollmentId);

    EnrollmentDto create(UUID studentId, EnrollmentRequest request);

    EnrollmentDto update(UUID studentId, UUID enrollmentId, EnrollmentRequest request);

    void delete(UUID studentId, UUID enrollmentId);

    List<EnrollmentTermDto> listTerms(UUID enrollmentId, UUID studentId);

    Optional<UUID> findActiveClassSectionIdByStudentId(UUID studentId);

    List<EnrollmentTerm> findActiveEnrollmentTermsByStudentIdAndSchoolId(UUID studentId, UUID schoolId);

    EnrollmentTerm findEnrollmentTermById(UUID id);
}
