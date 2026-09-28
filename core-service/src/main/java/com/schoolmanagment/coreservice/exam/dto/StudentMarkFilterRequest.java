package com.schoolmanagment.coreservice.exam.dto;

import com.schoolmanagment.coreservice.exam.enums.MarkStatus;
import com.schoolmanagment.coreservice.exam.enums.MarkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentMarkFilterRequest {

    private UUID studentId;

    private UUID subjectId;

    private MarkType type;

    private MarkStatus status;

    private String sortBy;

    private String sortDirection = "DESC";

    private int page = 0;

    private int size = 10;
}
