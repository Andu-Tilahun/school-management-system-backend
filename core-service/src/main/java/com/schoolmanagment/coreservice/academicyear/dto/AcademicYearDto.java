package com.schoolmanagment.coreservice.academicyear.dto;

import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYearDto {

    private UUID id;
    private UUID schoolId;
    private String acYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
    private List<TermDto> terms;
    private LocalDateTime createdAt;
    private String createdByName;
    private String updatedByName;

    public static AcademicYearDto fromEntity(AcademicYear academicYear) {
        List<TermDto> terms = academicYear.getTerms() == null
                ? List.of()
                : academicYear.getTerms().stream().map(TermDto::fromEntity).toList();

        return AcademicYearDto.builder()
                .id(academicYear.getId())
                .schoolId(academicYear.getSchoolId())
                .acYear(academicYear.getAcYear())
                .startDate(academicYear.getStartDate())
                .endDate(academicYear.getEndDate())
                .active(academicYear.getActive())
                .terms(terms)
                .createdAt(academicYear.getCreatedAt())
                .createdByName(academicYear.getCreatedByName())
                .updatedByName(academicYear.getUpdatedByName())
                .build();
    }
}
