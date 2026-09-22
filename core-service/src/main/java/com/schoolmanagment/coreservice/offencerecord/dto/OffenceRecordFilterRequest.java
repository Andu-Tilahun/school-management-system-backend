package com.schoolmanagment.coreservice.offencerecord.dto;

import com.schoolmanagment.coreservice.offencerecord.enums.OffenceRecordStatus;
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
public class OffenceRecordFilterRequest {

    private UUID schoolId;

    private UUID enrollmentId;

    private UUID studentId;

    private PenaltyTrigger penaltyTrigger;

    private SourceModule sourceModule;

    private OffenceRecordStatus status;

    private String sortBy;

    private String sortDirection = "DESC";

    private int page = 0;

    private int size = 10;
}
