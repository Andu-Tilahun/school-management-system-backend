package com.schoolmanagment.coreservice.tenant.mapper;

import com.schoolmanagment.coreservice.tenant.dto.TenantDto;
import com.schoolmanagment.coreservice.tenant.dto.TenantRequest;
import com.schoolmanagment.coreservice.tenant.entity.Tenant;
import org.springframework.stereotype.Component;

@Component
public class TenantMapper {

    public TenantDto toDto(Tenant tenant) {
        return TenantDto.builder()
                .id(tenant.getId())
                .tenantCode(tenant.getTenantCode())
                .tenantName(tenant.getTenantName())
                .website(tenant.getWebsite())
                .build();
    }

    public Tenant toEntity(TenantRequest request) {
        return Tenant.builder()
                .tenantName(request.getTenantName())
                .website(request.getWebsite())
                .build();
    }

    public void updateEntity(Tenant tenant, TenantRequest request) {
        tenant.setTenantName(request.getTenantName());
        tenant.setWebsite(request.getWebsite());
    }
}
