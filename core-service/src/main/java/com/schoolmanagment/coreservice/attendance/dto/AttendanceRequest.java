package com.schoolmanagment.coreservice.attendance.dto;

import com.schoolmanagment.coreservice.attendance.enums.AttendanceStatus;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AttendanceRequest {

    @NotNull(message = "Enrollment is required")
    private UUID enrollmentId;

    @NotNull(message = "Penalty trigger is required")
    private PenaltyTrigger penaltyTrigger;

    @NotNull(message = "Date occurred is required")
    @PastOrPresent(message = "Date occurred must be in the past or present")
    private LocalDate dateOccurred;

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    @NotNull(message = "Academic year is required")
    private UUID academicYearId;
}
