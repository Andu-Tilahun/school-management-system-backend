package com.schoolmanagment.coreservice.exam.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.exam.dto.GradeStudentMarkRequest;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkFilterRequest;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import com.schoolmanagment.coreservice.exam.entity.StudentMark;
import com.schoolmanagment.coreservice.exam.entity.SubjectTotal;
import com.schoolmanagment.coreservice.exam.enums.MarkStatus;
import com.schoolmanagment.coreservice.exam.enums.PassFailStatus;
import com.schoolmanagment.coreservice.exam.mapper.StudentMarkMapper;
import com.schoolmanagment.coreservice.exam.repository.StudentMarkRepository;
import com.schoolmanagment.coreservice.exam.repository.SubjectTotalRepository;
import com.schoolmanagment.coreservice.exam.specification.StudentMarkSpecification;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import com.schoolmanagment.coreservice.student.repository.EnrollmentTermRepository;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentMarkServiceImpl implements StudentMarkService {

    private final StudentMarkRepository studentMarkRepository;
    private final SubjectTotalRepository subjectTotalRepository;
    private final SubjectRepository subjectRepository;
    private final EnrollmentTermRepository enrollmentTermRepository;
    private final StudentMarkMapper studentMarkMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<StudentMarkDto> list(int page, int size) {
        StudentMarkFilterRequest request = StudentMarkFilterRequest.builder()
                .page(page)
                .size(size)
                .sortBy("id")
                .sortDirection("DESC")
                .build();
        return filter(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentMarkDto> filter(StudentMarkFilterRequest request) {
        StudentMarkFilterRequest effective = request != null ? request : new StudentMarkFilterRequest();
        int page = Math.max(effective.getPage(), 0);
        int size = effective.getSize() > 0 ? effective.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);
        return studentMarkRepository.findAll(new StudentMarkSpecification(effective), pageable)
                .map(studentMarkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentMarkDto getById(UUID id) {
        return studentMarkMapper.toDto(findActiveById(id));
    }

    @Override
    @Transactional
    public StudentMarkDto register(StudentMarkRequest request) {

        EnrollmentTerm enrollmentTerm = enrollmentTermRepository
                .findById(request.getEnrollmentTermId())
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentTerm not found"));

        if (enrollmentTerm.getStatus() != EnrollmentTermStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Cannot register a mark for a non-active term registration");
        }

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        studentMarkRepository
                .findByEnrollmentTermIdAndSubjectIdAndTypeAndActiveTrue(
                        enrollmentTerm.getId(), subject.getId(), request.getType())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Student is already registered for this subject/type");
                });

        boolean hasMark = request.getStudMark() != null;

        boolean isAbsent = Boolean.TRUE.equals(request.getMarkAbsent());

        if (hasMark && isAbsent) {
            throw new IllegalArgumentException(
                    "Cannot provide both studMark and markAbsent=true");
        }

        StudentMark mark = studentMarkMapper.toEntity(request, enrollmentTerm, subject);

        if (isAbsent) {
            mark.setStatus(MarkStatus.ABSENT);
            mark.setStudMark(null);
        } else if (hasMark) {
            mark.setStudMark(request.getStudMark());
            mark.setStatus(MarkStatus.GRADED);
        }

        StudentMark saved = studentMarkRepository.save(mark);

        if (saved.getStatus() == MarkStatus.GRADED || saved.getStatus() == MarkStatus.ABSENT) {
            recalculate(saved.getEnrollmentTerm().getId(), saved.getSubject().getId());
        }

        return studentMarkMapper.toDto(saved);
    }

    @Override
    @Transactional
    public StudentMarkDto grade(UUID id, GradeStudentMarkRequest request) {
        StudentMark mark = findActiveById(id);
        if (mark.getStatus() == MarkStatus.WITHDRAWN) {
            throw new BadRequestException("Cannot grade a withdrawn mark");
        }
        mark.setStudMark(request.getStudMark());
        mark.setStatus(MarkStatus.GRADED);
        StudentMark saved = studentMarkRepository.save(mark);
        recalculate(saved.getEnrollmentTerm().getId(), saved.getSubject().getId());
        return studentMarkMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        StudentMark mark = findActiveById(id);
        UUID enrollmentTermId = mark.getEnrollmentTerm().getId();
        UUID subjectId = mark.getSubject().getId();
        boolean affectsTotal = mark.getStatus() == MarkStatus.GRADED || mark.getStatus() == MarkStatus.ABSENT;
        mark.setActive(false);
        studentMarkRepository.save(mark);
        if (affectsTotal) {
            recalculate(enrollmentTermId, subjectId);
        }
    }

    private StudentMark findActiveById(UUID id) {
        StudentMark mark = studentMarkRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student mark not found with id: " + id));
        if (UserContext.current().hasSchoolAdminPolicy()) {
            UserContext.current().getCurrentExternalId().ifPresent(schoolId -> {
                if (mark.getSchoolId() != null && !schoolId.equals(mark.getSchoolId())) {
                    throw new ResourceNotFoundException("Student mark not found with id: " + id);
                }
            });
        }
        return mark;
    }

    @Transactional
    public SubjectTotal recalculate(UUID enrollmentTermId, UUID subjectId) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        EnrollmentTerm enrollmentTerm = enrollmentTermRepository.findById(enrollmentTermId)
                .orElseThrow(() -> new ResourceNotFoundException("EnrollmentTerm not found"));

        List<StudentMark> gradedMarks = studentMarkRepository
                .findByEnrollmentTermIdAndSubjectIdAndStatusAndActiveTrue(
                        enrollmentTermId, subjectId, MarkStatus.GRADED);

        double weightedSum = 0.0;
        double weightUsed = 0.0;

        for (StudentMark mark : gradedMarks) {
            Double weight = mark.getTotalMarkWeight();
            if (weight == null || mark.getStudMark() == null) {
                continue;
            }
            weightedSum += mark.getStudMark() * weight;
            weightUsed += weight;
        }

        if (weightUsed == 0.0) {
            subjectTotalRepository
                    .findByEnrollmentTermIdAndSubjectId(enrollmentTermId, subjectId)
                    .ifPresent(existing -> {
                        existing.setActive(false);
                        subjectTotalRepository.save(existing);
                    });
            return null;
        }

        double totalMark = weightedSum / weightUsed;

        PassFailStatus status = totalMark >= 50
                ? PassFailStatus.PASS
                : PassFailStatus.FAIL;

        SubjectTotal subjectTotal = subjectTotalRepository
                .findByEnrollmentTermIdAndSubjectId(enrollmentTermId, subjectId)
                .orElseGet(() -> SubjectTotal.builder()
                        .enrollmentTerm(enrollmentTerm)
                        .subject(subject)
                        .active(true)
                        .build());

        subjectTotal.setActive(true);
        subjectTotal.setTotalMark(totalMark);
        subjectTotal.setStatus(status);

        return subjectTotalRepository.save(subjectTotal);
    }
}
