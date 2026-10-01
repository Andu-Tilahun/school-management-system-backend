package com.schoolmanagment.coreservice.tenant.service;

import com.schoolmanagment.coreservice.tenant.dto.TenantDto;
import com.schoolmanagment.coreservice.tenant.dto.TenantFilterRequest;
import com.schoolmanagment.coreservice.tenant.dto.TenantRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface TenantService {
    TenantDto createTenant(TenantRequest request);
    TenantDto getTenantById(UUID id);
    List<TenantDto> getAllTenants();
    Page<TenantDto> filterTenants(TenantFilterRequest request);
    TenantDto updateTenant(UUID id, TenantRequest request);
    void deleteTenant(UUID id);
}
