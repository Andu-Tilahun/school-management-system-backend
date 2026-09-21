package com.schoolmanagment.coreservice.penalty.dto;

import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PenaltyRuleRequest {

    @NotNull(message = "Penalty trigger is required")
    private PenaltyTrigger penaltyTrigger;

    @NotNull(message = "Penalty type is required")
    private PenaltyType penaltyType;

    @NotNull(message = "Occurrence number is required")
    @Min(value = 1, message = "Occurrence number must be at least 1")
    private Integer occurrenceNumber;
}
