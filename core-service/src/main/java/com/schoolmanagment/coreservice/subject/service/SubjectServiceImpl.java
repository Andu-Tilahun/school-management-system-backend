package com.schoolmanagment.coreservice.subject.service;


import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import com.schoolmanagment.coreservice.grade.service.GradeService;
import com.schoolmanagment.coreservice.subject.dto.SubjectDto;
import com.schoolmanagment.coreservice.subject.dto.SubjectFilterRequest;
import com.schoolmanagment.coreservice.subject.dto.SubjectRequest;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import com.schoolmanagment.coreservice.subject.mapper.SubjectMapper;
import com.schoolmanagment.coreservice.subject.repository.SubjectRepository;
import com.schoolmanagment.coreservice.subject.specification.SubjectSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService{
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final GradeService gradeService;

    @Override
    public Subject findActiveSubjectById(UUID id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
        if (subject.getStatus() != SubjectStatus.ACTIVE) {
            throw new BadRequestException("Subject is not active: " + subject.getSubjectName());
        }
        return subject;
    }

    @Override
    public SubjectDto createSubject(SubjectRequest request) {
        Grade grade = gradeService.findActiveGradeById(request.getGradeId());
        if (subjectRepository.existsByGrade_IdAndSubjectCode(grade.getId(), request.getSubjectCode())) {
            throw new BadRequestException("Subject code already exists for this grade: " + request.getSubjectCode());
        }
        Subject subject = subjectMapper.toEntity(request, grade);
        return subjectMapper.toDto(subjectRepository.save(subject));
    }

    @Override
    public SubjectDto getSubjectById(UUID id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Subject not found: " + id));
        return subjectMapper.toDto(subject);
    }

    @Override
    public List<SubjectDto> getSubjects(SubjectFilterRequest filterRequest) {
        Specification<Subject> spec = new SubjectSpecification(filterRequest);
        return subjectRepository.findAll(spec).stream()
                .map(subjectMapper::toDto)
                .toList();
    }

    @Override
    public SubjectDto updateSubject(UUID id, SubjectRequest request) {

        Subject subject =subjectRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Subject not found: " + id));
        Grade grade = gradeService.findActiveGradeById(request.getGradeId());

        if (subjectRepository.existsByGrade_IdAndSubjectCodeAndIdNot(
                grade.getId(), request.getSubjectCode(), id)) {
            throw new BadRequestException("Subject code already exists for this grade: " + request.getSubjectCode());
        }

        subjectMapper.updateEntity(subject, request, grade);
        return subjectMapper.toDto(subjectRepository.save(subject));

    }

    @Override
    public void deleteSubject(UUID id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject not found: " + id);
        }
        subjectRepository.deleteById(id);
    }
}
