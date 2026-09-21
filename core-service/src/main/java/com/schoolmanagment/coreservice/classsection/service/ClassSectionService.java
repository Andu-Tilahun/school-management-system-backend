package com.schoolmanagment.coreservice.classsection.service;

import com.schoolmanagment.coreservice.classsection.dto.ClassSectionDto;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionFilterRequest;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionRequest;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ClassSectionService {

    ClassSection findActiveClassSectionById(UUID id);

    Page<ClassSectionDto> getAllClassSections(int page, int size);

    Page<ClassSectionDto> filterClassSections(ClassSectionFilterRequest request);

    ClassSectionDto getClassSectionById(UUID id);

    ClassSectionDto createClassSection(ClassSectionRequest request);

    ClassSectionDto updateClassSection(UUID id, ClassSectionRequest request);

    void deleteClassSection(UUID id);
}
