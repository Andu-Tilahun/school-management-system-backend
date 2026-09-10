package com.schoolmanagment.coreservice.classsection.dto;

import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSectionDto {

    private UUID id;
    private UUID schoolId;
    private UUID gradeId;
    private String gradeName;
    private LocalDateTime createdAt;

    public static ClassSectionDto fromEntity(ClassSection classSection) {
        Grade grade = classSection.getGrade();
        return ClassSectionDto.builder()
                .id(classSection.getId())
                .schoolId(classSection.getSchoolId())
                .gradeId(grade != null ? grade.getId() : null)
                .gradeName(grade != null ? grade.getName() : null)
                .createdAt(classSection.getCreatedAt())
                .build();
    }
}
