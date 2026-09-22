package com.schoolmanagment.coreservice.offencerecord.dto;

import com.schoolmanagment.coreservice.offencerecord.enums.OffenceRecordStatus;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class OffenceRecordRequest {

    @NotNull(message = "Enrollment is required")
    private UUID enrollmentId;

    @NotNull(message = "Penalty trigger is required")
    private PenaltyTrigger penaltyTrigger;

    @NotNull(message = "Date occurred is required")
    @PastOrPresent(message = "Date occurred must be in the past or present")
    private LocalDate dateOccurred;

    @NotNull(message = "Status is required")
    private OffenceRecordStatus status;

    @Size(max = 500, message = "Remark must be at most 500 characters")
    private String remark;
}
