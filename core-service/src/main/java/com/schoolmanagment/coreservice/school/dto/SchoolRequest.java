package com.schoolmanagment.coreservice.school.dto;

import com.schoolmanagment.coreservice.school.enums.SchoolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolRequest {

    @NotNull(message = "Tenant is required")
    private UUID tenantId;

    @NotNull(message = "School type is required")
    private SchoolType schoolType;

    @NotBlank(message = "School name is required")
    private String schoolName;

    private String website;
}