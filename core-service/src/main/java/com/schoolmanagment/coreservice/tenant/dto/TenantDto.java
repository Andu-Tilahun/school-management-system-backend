package com.schoolmanagment.coreservice.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {
    private UUID id;
    private String tenantCode;
    private String tenantName;
    private String website;
    private String createdByName;
    private String updatedByName;
}
