package com.schoolmanagment.coreservice.exam.service;

import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import com.schoolmanagment.coreservice.exam.entity.StudentMark;
import com.schoolmanagment.coreservice.exam.entity.SubjectTotal;
import com.schoolmanagment.coreservice.exam.enums.MarkStatus;
import com.schoolmanagment.coreservice.exam.enums.PassFailStatus;
import com.schoolmanagment.coreservice.exam.mapper.StudentMarkMapper;
import com.schoolmanagment.coreservice.exam.repository.StudentMarkRepository;
import com.schoolmanagment.coreservice.exam.repository.SubjectTotalRepository;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import com.schoolmanagment.coreservice.student.repository.EnrollmentTermRepository;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
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
                .findByEnrollmentTermIdAndSubjectIdAndType(
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
            throw new IllegalStateException(
                    "No graded marks with defined weights exist yet for this subject/term");
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

        subjectTotal.setTotalMark(totalMark);
        subjectTotal.setStatus(status);

        return subjectTotalRepository.save(subjectTotal);
    }
}
