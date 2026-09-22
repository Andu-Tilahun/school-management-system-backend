package com.schoolmanagment.coreservice.academicyear.dto;

import com.schoolmanagment.coreservice.academicyear.enums.Semester;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class TermRequest {

    @NotNull(message = "Academic year is required")
    private UUID academicYearId;

    @NotNull(message = "Semester is required")
    private Semester semester;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active = true;
}
