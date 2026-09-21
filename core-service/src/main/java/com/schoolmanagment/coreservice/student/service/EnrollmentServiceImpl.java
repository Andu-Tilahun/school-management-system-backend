package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.helper.AcademicYearHelper;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.classsection.service.ClassSectionService;
import com.schoolmanagment.coreservice.student.dto.EnrollmentDto;
import com.schoolmanagment.coreservice.student.dto.EnrollmentFilterRequest;
import com.schoolmanagment.coreservice.student.dto.EnrollmentRequest;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.mapper.EnrollmentMapper;
import com.schoolmanagment.coreservice.student.repository.EnrollmentRepository;
import com.schoolmanagment.coreservice.student.specification.EnrollmentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final ClassSectionService classSectionService;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentDto> listByStudent(UUID studentId, EnrollmentFilterRequest filter) {
        studentService.findActiveStudentById(studentId);
        EnrollmentFilterRequest effectiveFilter = filter != null ? filter : new EnrollmentFilterRequest();
        Pageable pageable = PageRequest.of(effectiveFilter.getPage(), effectiveFilter.getSize());
        return enrollmentRepository.findAll(new EnrollmentSpecification(studentId, effectiveFilter), pageable)
                .map(enrollmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentDto getById(UUID studentId, UUID enrollmentId) {
        return enrollmentMapper.toDto(findActiveEnrollmentForStudent(studentId, enrollmentId));
    }

    @Override
    @Transactional
    public EnrollmentDto create(UUID studentId, EnrollmentRequest request) {
        Student student = studentService.findActiveStudentById(studentId);
        ClassSection classSection = classSectionService.findActiveClassSectionById(request.getClassSectionId());
        AcademicYear academicYear = AcademicYearHelper.getActiveAcademicYear();
        validateEnrollmentNotTaken(student.getSchoolId(), student.getId(), academicYear.getId(), null);
        return enrollmentMapper.toDto(
                enrollmentRepository.save(enrollmentMapper.toEntity(request, student, classSection, academicYear))
        );
    }

    @Override
    @Transactional
    public EnrollmentDto update(UUID studentId, UUID enrollmentId, EnrollmentRequest request) {
        Enrollment enrollment = findActiveEnrollmentForStudent(studentId, enrollmentId);
        ClassSection classSection = classSectionService.findActiveClassSectionById(request.getClassSectionId());
        AcademicYear academicYear = AcademicYearHelper.getActiveAcademicYear();
        validateEnrollmentNotTaken(enrollment.getSchoolId(), studentId, academicYear.getId(), enrollmentId);
        enrollmentMapper.updateEntity(enrollment, request, classSection, academicYear);
        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional
    public void delete(UUID studentId, UUID enrollmentId) {
        Enrollment enrollment = findActiveEnrollmentForStudent(studentId, enrollmentId);
        enrollment.setActive(false);
        enrollmentRepository.save(enrollment);
    }

    private Enrollment findActiveEnrollmentForStudent(UUID studentId, UUID enrollmentId) {
        studentService.findActiveStudentById(studentId);
        Enrollment enrollment = enrollmentRepository.findByIdAndActiveTrue(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        if (enrollment.getStudent() == null || !studentId.equals(enrollment.getStudent().getId())) {
            throw new ResourceNotFoundException(
                    "Enrollment not found with id: " + enrollmentId + " for student: " + studentId);
        }
        return enrollment;
    }

    private void validateEnrollmentNotTaken(UUID schoolId, UUID studentId, UUID academicYearId, UUID excludeId) {
        boolean taken = excludeId == null
                ? enrollmentRepository.existsBySchoolIdAndStudent_IdAndAcademicYear_Id(schoolId, studentId, academicYearId)
                : enrollmentRepository.existsBySchoolIdAndStudent_IdAndAcademicYear_IdAndIdNot(
                schoolId, studentId, academicYearId, excludeId);
        if (taken) {
            throw new BadRequestException("Student is already enrolled for this academic year in this school");
        }
    }
}
