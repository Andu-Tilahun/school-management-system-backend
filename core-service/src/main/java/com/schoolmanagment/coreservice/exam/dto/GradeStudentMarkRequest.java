package com.schoolmanagment.coreservice.exam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeStudentMarkRequest {

    @NotNull
    private Double studMark;
}
