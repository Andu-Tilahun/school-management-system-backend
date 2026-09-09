package com.schoolmanagment.coreservice.school.mapper;

import com.schoolmanagment.coreservice.school.dto.SchoolDto;
import com.schoolmanagment.coreservice.school.entity.School;
import org.springframework.stereotype.Component;

@Component
public class SchoolMapper {

    public SchoolDto toDto(School school) {
        return SchoolDto.builder()
                .id(school.getId())
                .tenantId(school.getTenant().getId())
                .tenantName(school.getTenant().getTenantName())
                .schoolType(school.getSchoolType())
                .schoolName(school.getSchoolName())
                .website(school.getWebsite())
                .build();
    }

    public void updateEntity(School school, com.schoolmanagment.coreservice.school.dto.SchoolRequest request) {
        school.setSchoolType(request.getSchoolType());
        school.setSchoolName(request.getSchoolName());
        school.setWebsite(request.getWebsite());
    }
}