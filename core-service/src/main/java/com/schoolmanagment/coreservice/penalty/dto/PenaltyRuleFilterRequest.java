package com.schoolmanagment.coreservice.penalty.dto;

import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyType;
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
public class PenaltyRuleFilterRequest {

    private UUID schoolId;

    private PenaltyTrigger penaltyTrigger;

    private SourceModule sourceModule;

    private PenaltyType penaltyType;

    private Integer occurrenceNumber;

    private String searchText;

    private String sortBy;

    private String sortDirection = "ASC";

    private int page = 0;

    private int size = 10;
}
