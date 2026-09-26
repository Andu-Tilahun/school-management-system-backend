package com.schoolmanagment.coreservice.classsection.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ClassSectionHomeroomRequest {

    @NotNull(message = "Class section is required")
    private UUID classSectionId;

    @NotNull(message = "Teacher is required")
    private UUID teacherId;
}
