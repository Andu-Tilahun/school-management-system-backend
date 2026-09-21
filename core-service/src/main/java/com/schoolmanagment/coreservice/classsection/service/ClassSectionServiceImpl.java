package com.schoolmanagment.coreservice.classsection.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionDto;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionFilterRequest;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionRequest;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.classsection.mapper.ClassSectionMapper;
import com.schoolmanagment.coreservice.classsection.repository.ClassSectionRepository;
import com.schoolmanagment.coreservice.classsection.specification.ClassSectionSpecification;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import com.schoolmanagment.coreservice.grade.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassSectionServiceImpl implements ClassSectionService {

    private final ClassSectionRepository classSectionRepository;
    private final ClassSectionMapper classSectionMapper;
    private final GradeService gradeService;

    @Override
    @Transactional(readOnly = true)
    public Page<ClassSectionDto> getAllClassSections(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return classSectionRepository.findByActiveTrue(pageable)
                .map(classSectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassSectionDto> filterClassSections(ClassSectionFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return classSectionRepository.findAll(new ClassSectionSpecification(request), pageable)
                .map(classSectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassSectionDto getClassSectionById(UUID id) {
        return classSectionMapper.toDto(findActiveClassSectionById(id));
    }

    @Override
    @Transactional
    public ClassSectionDto createClassSection(ClassSectionRequest request) {
        Grade grade = gradeService.findActiveGradeById(request.getGradeId());
        validateSectionNotTaken(grade.getId(), null);
        return classSectionMapper.toDto(classSectionRepository.save(classSectionMapper.toEntity(request, grade)));
    }

    @Override
    @Transactional
    public ClassSectionDto updateClassSection(UUID id, ClassSectionRequest request) {
        ClassSection classSection = findActiveClassSectionById(id);
        Grade grade = gradeService.findActiveGradeById(request.getGradeId());
        validateSectionNotTaken(grade.getId(), id);
        classSectionMapper.updateEntity(classSection, grade);
        return classSectionMapper.toDto(classSectionRepository.save(classSection));
    }

    @Override
    @Transactional
    public void deleteClassSection(UUID id) {
        ClassSection classSection = findActiveClassSectionById(id);
        classSection.setActive(false);
        classSectionRepository.save(classSection);
    }

    @Override
    public ClassSection findActiveClassSectionById(UUID id) {
        return classSectionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class section not found with id: " + id));
    }

    private void validateSectionNotTaken(UUID gradeId, UUID excludeId) {
        boolean taken = excludeId == null
                ? classSectionRepository.existsByGrade_Id(gradeId)
                : classSectionRepository.existsByGrade_IdAndIdNot(gradeId, excludeId);
        if (taken) {
            throw new BadRequestException("Class section already exists for this grade");
        }
    }
}
