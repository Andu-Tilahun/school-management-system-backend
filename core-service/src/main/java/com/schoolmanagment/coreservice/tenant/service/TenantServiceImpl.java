package com.schoolmanagment.coreservice.tenant.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.coreservice.tenant.dto.TenantDto;
import com.schoolmanagment.coreservice.tenant.dto.TenantRequest;
import com.schoolmanagment.coreservice.tenant.entity.Tenant;
import com.schoolmanagment.coreservice.tenant.mapper.TenantMapper;
import com.schoolmanagment.coreservice.tenant.repository.TenantRepository;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Override
    public TenantDto createTenant(TenantRequest request) {
        if (tenantRepository.existsByTenantNameIgnoreCase(request.getTenantName())) {
            throw new BadRequestException("Tenant already exists: " + request.getTenantName());
        }
        Tenant tenant = tenantMapper.toEntity(request);
        tenant.setTenantCode(generateTenantCode());
        return tenantMapper.toDto(tenantRepository.save(tenant));
    }

    private String generateTenantCode() {
        long count = tenantRepository.count();
        return String.format("TEN%02d", count + 1);
    }

    @Override
    public TenantDto getTenantById(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));
        return tenantMapper.toDto(tenant);
    }

    @Override
    public List<TenantDto> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(tenantMapper::toDto)
                .toList();
    }

    @Override
    public TenantDto updateTenant(UUID id, TenantRequest request) {
        Tenant existing = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));
        tenantMapper.updateEntity(existing, request);
        return tenantMapper.toDto(tenantRepository.save(existing));
    }

    @Override
    public void deleteTenant(UUID id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tenant not found: " + id);
        }
        tenantRepository.deleteById(id);
    }
}