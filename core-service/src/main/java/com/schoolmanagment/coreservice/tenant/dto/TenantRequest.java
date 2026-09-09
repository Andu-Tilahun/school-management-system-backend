package com.schoolmanagment.coreservice.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequest {
    @NotBlank(message = "Tenant name is required")
    private String tenantName;
    private String website;
}
