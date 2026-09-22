package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.entity.Term;
import com.schoolmanagment.coreservice.academicyear.helper.AcademicYearHelper;
import com.schoolmanagment.coreservice.academicyear.repository.TermRepository;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.classsection.service.ClassSectionService;
import com.schoolmanagment.coreservice.student.dto.EnrollmentDto;
import com.schoolmanagment.coreservice.student.dto.EnrollmentFilterRequest;
import com.schoolmanagment.coreservice.student.dto.EnrollmentRequest;
import com.schoolmanagment.coreservice.student.dto.EnrollmentTermDto;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import com.schoolmanagment.coreservice.student.mapper.EnrollmentMapper;
import com.schoolmanagment.coreservice.student.mapper.EnrollmentTermMapper;
import com.schoolmanagment.coreservice.student.repository.EnrollmentRepository;
import com.schoolmanagment.coreservice.student.repository.EnrollmentTermRepository;
import com.schoolmanagment.coreservice.student.specification.EnrollmentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final ClassSectionService classSectionService;
    private final EnrollmentMapper enrollmentMapper;
    private final TermRepository termRepository;
    private final EnrollmentTermRepository enrollmentTermRepository;
    private final EnrollmentTermMapper enrollmentTermMapper;

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
        Enrollment enrollment = enrollmentRepository.save(enrollmentMapper.toEntity(request, student, classSection, academicYear));
        registerForTerm(enrollment.getId(), null);
        return enrollmentMapper.toDto(enrollment);
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

    @Transactional
    public EnrollmentTermDto registerForTerm(UUID enrollmentId, UUID termId) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found"));

        if (enrollmentTermRepository.existsByEnrollmentIdAndTermId(enrollmentId, termId)) {
            throw new IllegalStateException("Already registered for this term");
        }


        enrollmentTermRepository
                .findByEnrollmentIdAndStatus(enrollmentId, EnrollmentTermStatus.ACTIVE)
                .ifPresent(previous -> {
                    previous.setStatus(EnrollmentTermStatus.COMPLETED);
                    enrollmentTermRepository.save(previous);
                });


        EnrollmentTerm newRegistration = EnrollmentTerm.builder()
                .enrollment(enrollment)
                .term(term)
                .status(EnrollmentTermStatus.ACTIVE)
                .registeredAt(LocalDate.now())
                .build();

        return enrollmentTermMapper.toDto(
                enrollmentTermRepository.save(newRegistration));
    }

    @Transactional
    public boolean autoRegisterActiveStudentsForTerm(UUID newTermId) {

        List<EnrollmentTerm> currentlyActive =
                enrollmentTermRepository.findAllByStatus(EnrollmentTermStatus.ACTIVE);

        int registered = 0, skipped = 0;
        List<String> failures = new ArrayList<>();

        for (EnrollmentTerm current : currentlyActive) {
            try {
                this.registerForTerm(
                        current.getEnrollment().getId(), newTermId);
                registered++;
            } catch (IllegalStateException e) {
                skipped++;
                failures.add(current.getEnrollment().getId() + ": " + e.getMessage());
            }
        }

        return true;
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
