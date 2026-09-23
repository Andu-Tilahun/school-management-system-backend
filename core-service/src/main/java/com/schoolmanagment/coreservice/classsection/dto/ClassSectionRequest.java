package com.schoolmanagment.coreservice.classsection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ClassSectionRequest {

    @NotNull(message = "Grade is required")
    private UUID gradeId;

    @NotBlank(message = "Name is required")
    private String name;
}
