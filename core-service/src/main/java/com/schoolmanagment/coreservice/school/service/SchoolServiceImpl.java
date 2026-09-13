package com.schoolmanagment.coreservice.school.service;

import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.school.dto.SchoolDto;
import com.schoolmanagment.coreservice.school.dto.SchoolFilterRequest;
import com.schoolmanagment.coreservice.school.dto.SchoolRequest;
import com.schoolmanagment.coreservice.school.entity.School;
import com.schoolmanagment.coreservice.school.mapper.SchoolMapper;
import com.schoolmanagment.coreservice.school.repository.SchoolRepository;
import com.schoolmanagment.coreservice.school.specification.SchoolSpecification;
import com.schoolmanagment.coreservice.tenant.entity.Tenant;
import com.schoolmanagment.coreservice.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;
    private final TenantRepository tenantRepository;
    private final SchoolMapper schoolMapper;

    @Override
    public SchoolDto createSchool(SchoolRequest request) {
        UUID tenantId = resolveTenantId(request.getTenantId());
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantId));

        School school = School.builder()
                .tenant(tenant)
                .schoolType(request.getSchoolType())
                .schoolName(request.getSchoolName())
                .website(request.getWebsite())
                .build();

        School saved = schoolRepository.save(school);
        return schoolMapper.toDto(saved);
    }

    @Override
    public SchoolDto getSchoolById(UUID id) {
        return schoolMapper.toDto(findSchoolInScope(id));
    }

    @Override
    public List<SchoolDto> getAllSchools(SchoolFilterRequest filterRequest) {
        return schoolRepository.findAll(new SchoolSpecification(filterRequest)).stream()
                .map(schoolMapper::toDto)
                .toList();
    }

    @Override
    public SchoolDto updateSchool(UUID id, SchoolRequest request) {
        School existing = findSchoolInScope(id);
        UUID tenantId = resolveTenantId(request.getTenantId());

        if (!existing.getTenant().getId().equals(tenantId)) {
            Tenant newTenant = tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantId));
            existing.setTenant(newTenant);
        }

        schoolMapper.updateEntity(existing, request);
        return schoolMapper.toDto(schoolRepository.save(existing));
    }

    @Override
    public void deleteSchool(UUID id) {
        findSchoolInScope(id);
        schoolRepository.deleteById(id);
    }

    private School findSchoolInScope(UUID id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found: " + id));
        assertSchoolInTenantScope(school);
        return school;
    }

    private void assertSchoolInTenantScope(School school) {
        if (!isTenantManagerScoped()) {
            return;
        }
        if (!school.getTenant().getId().equals(requiredTenantId())) {
            throw new ResourceNotFoundException("School not found: " + school.getId());
        }
    }

    private UUID resolveTenantId(UUID requestedTenantId) {
        if (isTenantManagerScoped()) {
            return requiredTenantId();
        }
        return requestedTenantId;
    }

    private boolean isTenantManagerScoped() {
        UserContext ctx = UserContext.current();
        return ctx != null && ctx.hasTenantManager();
    }

    private UUID requiredTenantId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No tenant id on the current authentication — cannot scope schools without one."));
    }
}