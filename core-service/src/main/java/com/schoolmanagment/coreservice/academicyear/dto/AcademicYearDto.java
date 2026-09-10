package com.schoolmanagment.coreservice.academicyear.dto;

import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
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
public class AcademicYearDto {

    private UUID id;
    private UUID schoolId;
    private String acYear;
    private String semester;
    private Boolean active;
    private LocalDateTime createdAt;

    public static AcademicYearDto fromEntity(AcademicYear academicYear) {
        return AcademicYearDto.builder()
                .id(academicYear.getId())
                .schoolId(academicYear.getSchoolId())
                .acYear(academicYear.getAcYear())
                .semester(academicYear.getSemester())
                .active(academicYear.getActive())
                .createdAt(academicYear.getCreatedAt())
                .build();
    }
}
