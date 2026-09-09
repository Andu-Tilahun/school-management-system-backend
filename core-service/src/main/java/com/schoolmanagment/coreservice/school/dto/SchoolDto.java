package com.schoolmanagment.coreservice.school.dto;

import com.schoolmanagment.coreservice.school.enums.SchoolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDto {
    private UUID id;
    private UUID tenantId;
    private String tenantName;
    private SchoolType schoolType;
    private String schoolName;
    private String website;
}