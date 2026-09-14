package com.schoolmanagment.coreservice.grade.dto;

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
public class GradeDto {

    private UUID id;
    private UUID schoolId;
    private String name;
    private LocalDateTime createdAt;
    private String createdByName;
    private String updatedByName;

    public static GradeDto fromEntity(Grade grade) {
        return GradeDto.builder()
                .id(grade.getId())
                .schoolId(grade.getSchoolId())
                .name(grade.getName())
                .createdAt(grade.getCreatedAt())
                .createdByName(grade.getCreatedByName())
                .updatedByName(grade.getUpdatedByName())
                .build();
    }
}
