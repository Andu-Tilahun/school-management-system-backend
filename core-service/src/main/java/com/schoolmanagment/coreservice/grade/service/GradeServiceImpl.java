package com.schoolmanagment.coreservice.grade.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.grade.dto.GradeDto;
import com.schoolmanagment.coreservice.grade.dto.GradeFilterRequest;
import com.schoolmanagment.coreservice.grade.dto.GradeRequest;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import com.schoolmanagment.coreservice.grade.mapper.GradeMapper;
import com.schoolmanagment.coreservice.grade.repository.GradeRepository;
import com.schoolmanagment.coreservice.grade.specification.GradeSpecification;
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
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<GradeDto> getAllGrades(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return gradeRepository.findByActiveTrue(pageable)
                .map(gradeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GradeDto> filterGrades(GradeFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return gradeRepository.findAll(new GradeSpecification(request), pageable)
                .map(gradeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeDto getGradeById(UUID id) {
        return gradeMapper.toDto(findActiveGradeById(id));
    }

    @Override
    @Transactional
    public GradeDto createGrade(GradeRequest request) {
        validateNameNotTaken(currentSchoolId(), request.getName(), null);
        return gradeMapper.toDto(gradeRepository.save(gradeMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public GradeDto updateGrade(UUID id, GradeRequest request) {
        Grade grade = findActiveGradeById(id);
        validateNameNotTaken(grade.getSchoolId(), request.getName(), id);
        gradeMapper.updateEntity(grade, request);
        return gradeMapper.toDto(gradeRepository.save(grade));
    }

    @Override
    @Transactional
    public void deleteGrade(UUID id) {
        Grade grade = findActiveGradeById(id);
        grade.setActive(false);
        gradeRepository.save(grade);
    }

    @Override
    public Grade findActiveGradeById(UUID id) {
        return gradeRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found with id: " + id));
    }

    private void validateNameNotTaken(UUID schoolId, String name, UUID excludeId) {
        boolean taken = excludeId == null
                ? gradeRepository.existsBySchoolIdAndNameIgnoreCase(schoolId, name)
                : gradeRepository.existsBySchoolIdAndNameIgnoreCaseAndIdNot(schoolId, name, excludeId);
        if (taken) {
            throw new BadRequestException("Grade with name '" + name + "' already exists in this school");
        }
    }

    private UUID currentSchoolId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No schoolId on the current authentication — cannot save a school-scoped entity without one."));
    }
}
