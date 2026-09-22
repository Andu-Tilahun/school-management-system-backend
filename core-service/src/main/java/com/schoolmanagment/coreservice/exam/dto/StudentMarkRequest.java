package com.schoolmanagment.coreservice.exam.dto;

import com.schoolmanagment.coreservice.exam.enums.MarkType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class StudentMarkRequest {

    @NotNull
    private UUID enrollmentTermId;

    @NotNull
    private UUID subjectId;

    @NotNull
    private MarkType type;

    private Double studMark;

    @Builder.Default
    private Boolean markAbsent = false;
}
