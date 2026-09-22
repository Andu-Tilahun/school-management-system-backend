package com.schoolmanagment.coreservice.academicyear.dto;

import com.schoolmanagment.coreservice.academicyear.entity.Term;
import com.schoolmanagment.coreservice.academicyear.enums.Semester;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermDto {

    private UUID id;
    private UUID academicYearId;
    private String acYear;
    private Semester semester;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;

    public static TermDto fromEntity(Term term) {
        return TermDto.builder()
                .id(term.getId())
                .academicYearId(term.getAcademicYear() != null ? term.getAcademicYear().getId() : null)
                .acYear(term.getAcademicYear() != null ? term.getAcademicYear().getAcYear() : null)
                .semester(term.getSemester())
                .startDate(term.getStartDate())
                .endDate(term.getEndDate())
                .active(term.getActive())
                .build();
    }
}
