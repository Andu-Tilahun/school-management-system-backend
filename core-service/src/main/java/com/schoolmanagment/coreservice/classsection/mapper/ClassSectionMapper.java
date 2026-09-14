package com.schoolmanagment.coreservice.classsection.mapper;

import com.schoolmanagment.coreservice.classsection.dto.ClassSectionDto;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionRequest;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import org.springframework.stereotype.Component;

@Component
public class ClassSectionMapper {

    public ClassSectionDto toDto(ClassSection classSection) {
        Grade grade = classSection.getGrade();
        return ClassSectionDto.builder()
                .id(classSection.getId())
                .schoolId(classSection.getSchoolId())
                .gradeId(grade != null ? grade.getId() : null)
                .gradeName(grade != null ? grade.getName() : null)
                .createdAt(classSection.getCreatedAt())
                .createdByName(classSection.getCreatedByName())
                .updatedByName(classSection.getUpdatedByName())
                .build();
    }

    public ClassSection toEntity(ClassSectionRequest request, Grade grade) {
        return ClassSection.builder()
                .grade(grade)
                .active(true)
                .build();
    }

    public void updateEntity(ClassSection classSection, Grade grade) {
        classSection.setGrade(grade);
    }
}
