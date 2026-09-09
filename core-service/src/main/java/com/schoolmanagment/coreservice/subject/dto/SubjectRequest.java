package com.schoolmanagment.coreservice.subject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequest {
    @NotNull(message = "School is required")
    private UUID schoolId;

    @NotBlank(message = "Subject code is required")
    private String subjectCode;

    @NotBlank(message = "Subject name is required")
    private String subjectName;
}
