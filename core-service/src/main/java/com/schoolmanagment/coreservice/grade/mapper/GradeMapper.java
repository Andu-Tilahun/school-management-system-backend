package com.schoolmanagment.coreservice.grade.mapper;

import com.schoolmanagment.coreservice.grade.dto.GradeDto;
import com.schoolmanagment.coreservice.grade.dto.GradeRequest;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import org.springframework.stereotype.Component;

@Component
public class GradeMapper {

    public GradeDto toDto(Grade grade) {
        return GradeDto.builder()
                .id(grade.getId())
                .schoolId(grade.getSchoolId())
                .name(grade.getName())
                .createdAt(grade.getCreatedAt())
                .build();
    }

    public Grade toEntity(GradeRequest request) {
        return Grade.builder()
                .name(request.getName())
                .active(true)
                .build();
    }

    public void updateEntity(Grade grade, GradeRequest request) {
        grade.setName(request.getName());
    }
}
