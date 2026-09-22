package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentFilterRequest {

    private UUID classSectionId;

    private EnrollmentStatus status;

    private int page = 0;

    private int size = 10;
}
