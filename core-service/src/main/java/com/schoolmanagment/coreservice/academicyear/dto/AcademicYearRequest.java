package com.schoolmanagment.coreservice.academicyear.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AcademicYearRequest {

    @NotBlank(message = "Academic year is required")
    private String acYear;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active = true;
}
