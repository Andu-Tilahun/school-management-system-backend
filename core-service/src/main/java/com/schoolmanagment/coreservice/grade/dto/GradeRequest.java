package com.schoolmanagment.coreservice.grade.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GradeRequest {

    @NotBlank(message = "Name is required")
    private String name;
}
