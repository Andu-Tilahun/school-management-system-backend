package com.schoolmanagment.coreservice.tenant.service;

import com.schoolmanagment.coreservice.tenant.dto.TenantDto;
import com.schoolmanagment.coreservice.tenant.dto.TenantRequest;
import java.util.List;
import java.util.UUID;

public interface TenantService {
    TenantDto createTenant(TenantRequest request);
    TenantDto getTenantById(UUID id);
    List<TenantDto> getAllTenants();
    TenantDto updateTenant(UUID id, TenantRequest request);
    void deleteTenant(UUID id);
}
