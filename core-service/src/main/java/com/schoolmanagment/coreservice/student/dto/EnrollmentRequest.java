package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class EnrollmentRequest {

    @NotNull(message = "Class section is required")
    private UUID classSectionId;

    @NotNull(message = "Academic year is required")
    private UUID academicYearId;

    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
}
