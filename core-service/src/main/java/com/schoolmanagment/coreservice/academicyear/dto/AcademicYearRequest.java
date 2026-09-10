package com.schoolmanagment.coreservice.academicyear.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AcademicYearRequest {

    @NotBlank(message = "Academic year is required")
    private String acYear;

    @NotBlank(message = "Semester is required")
    private String semester;

    private Boolean active = true;
}
