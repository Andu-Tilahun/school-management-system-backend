package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.repository.AcademicYearRepository;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.classsection.repository.ClassSectionRepository;
import com.schoolmanagment.coreservice.student.dto.EnrollmentDto;
import com.schoolmanagment.coreservice.student.dto.EnrollmentFilterRequest;
import com.schoolmanagment.coreservice.student.dto.EnrollmentRequest;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.mapper.EnrollmentMapper;
import com.schoolmanagment.coreservice.student.repository.EnrollmentRepository;
import com.schoolmanagment.coreservice.student.repository.StudentRepository;
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
    private final StudentRepository studentRepository;
    private final ClassSectionRepository classSectionRepository;
    private final AcademicYearRepository academicYearRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentDto> listByStudent(UUID studentId, EnrollmentFilterRequest filter) {
        findActiveStudentById(studentId);
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
        Student student = findActiveStudentById(studentId);
        ClassSection classSection = resolveActiveClassSection(request.getClassSectionId());
        AcademicYear academicYear = resolveActiveAcademicYear(request.getAcademicYearId());
        validateBelongsToSchool(classSection.getSchoolId(), student.getSchoolId(), "Class section");
        validateBelongsToSchool(academicYear.getSchoolId(), student.getSchoolId(), "Academic year");
        validateEnrollmentNotTaken(student.getSchoolId(), student.getId(), academicYear.getId(), null);
        return enrollmentMapper.toDto(
                enrollmentRepository.save(enrollmentMapper.toEntity(request, student, classSection, academicYear))
        );
    }

    @Override
    @Transactional
    public EnrollmentDto update(UUID studentId, UUID enrollmentId, EnrollmentRequest request) {
        Enrollment enrollment = findActiveEnrollmentForStudent(studentId, enrollmentId);
        ClassSection classSection = resolveActiveClassSection(request.getClassSectionId());
        AcademicYear academicYear = resolveActiveAcademicYear(request.getAcademicYearId());
        validateBelongsToSchool(classSection.getSchoolId(), enrollment.getSchoolId(), "Class section");
        validateBelongsToSchool(academicYear.getSchoolId(), enrollment.getSchoolId(), "Academic year");
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

    private Student findActiveStudentById(UUID studentId) {
        return studentRepository.findByIdAndActiveTrue(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
    }

    private Enrollment findActiveEnrollmentForStudent(UUID studentId, UUID enrollmentId) {
        findActiveStudentById(studentId);
        Enrollment enrollment = enrollmentRepository.findByIdAndActiveTrue(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        if (enrollment.getStudent() == null || !studentId.equals(enrollment.getStudent().getId())) {
            throw new ResourceNotFoundException(
                    "Enrollment not found with id: " + enrollmentId + " for student: " + studentId);
        }
        return enrollment;
    }

    private ClassSection resolveActiveClassSection(UUID classSectionId) {
        return classSectionRepository.findByIdAndActiveTrue(classSectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Class section not found with id: " + classSectionId));
    }

    private AcademicYear resolveActiveAcademicYear(UUID academicYearId) {
        return academicYearRepository.findByIdAndActiveTrue(academicYearId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found with id: " + academicYearId));
    }

    private void validateBelongsToSchool(UUID entitySchoolId, UUID schoolId, String label) {
        if (!schoolId.equals(entitySchoolId)) {
            throw new BadRequestException(label + " does not belong to this school");
        }
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
