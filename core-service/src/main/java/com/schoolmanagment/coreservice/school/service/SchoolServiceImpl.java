package com.schoolmanagment.coreservice.school.service;

import com.schoolmanagment.coreservice.school.dto.SchoolDto;
import com.schoolmanagment.coreservice.school.dto.SchoolRequest;
import com.schoolmanagment.coreservice.school.entity.School;
import com.schoolmanagment.coreservice.school.mapper.SchoolMapper;
import com.schoolmanagment.coreservice.school.repository.SchoolRepository;
import com.schoolmanagment.coreservice.tenant.entity.Tenant;
import com.schoolmanagment.coreservice.tenant.repository.TenantRepository;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;
    private final TenantRepository tenantRepository;
    private final SchoolMapper schoolMapper;

    @Override
    public SchoolDto createSchool(SchoolRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + request.getTenantId()));

        School school = School.builder()
                .tenant(tenant)
                .schoolType(request.getSchoolType())
                .schoolName(request.getSchoolName())
                .website(request.getWebsite())
                .build();

        return schoolMapper.toDto(schoolRepository.save(school));
    }

    @Override
    public SchoolDto getSchoolById(UUID id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found: " + id));
        return schoolMapper.toDto(school);
    }

    @Override
    public List<SchoolDto> getAllSchools() {
        return schoolRepository.findAll().stream()
                .map(schoolMapper::toDto)
                .toList();
    }

    @Override
    public SchoolDto updateSchool(UUID id, SchoolRequest request) {
        School existing = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found: " + id));

        if (!existing.getTenant().getId().equals(request.getTenantId())) {
            Tenant newTenant = tenantRepository.findById(request.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + request.getTenantId()));
            existing.setTenant(newTenant);
        }

        schoolMapper.updateEntity(existing, request);
        return schoolMapper.toDto(schoolRepository.save(existing));
    }

    @Override
    public void deleteSchool(UUID id) {
        if (!schoolRepository.existsById(id)) {
            throw new ResourceNotFoundException("School not found: " + id);
        }
        schoolRepository.deleteById(id);
    }
}