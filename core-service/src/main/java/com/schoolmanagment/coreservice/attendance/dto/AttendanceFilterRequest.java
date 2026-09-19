package com.schoolmanagment.coreservice.attendance.dto;

import com.schoolmanagment.coreservice.attendance.enums.AttendanceStatus;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceFilterRequest {

    private UUID schoolId;

    private UUID enrollmentId;

    private UUID studentId;

    private UUID academicYearId;

    private PenaltyTrigger penaltyTrigger;

    private SourceModule sourceModule;

    private AttendanceStatus status;

    private String sortBy;

    private String sortDirection = "DESC";

    private int page = 0;

    private int size = 10;
}
