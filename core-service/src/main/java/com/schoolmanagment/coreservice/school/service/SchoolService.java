package com.schoolmanagment.coreservice.school.service;

import com.schoolmanagment.coreservice.school.dto.SchoolDto;
import com.schoolmanagment.coreservice.school.dto.SchoolRequest;

import java.util.List;
import java.util.UUID;

public interface SchoolService {
    SchoolDto createSchool(SchoolRequest request);
    SchoolDto getSchoolById(UUID id);
    List<SchoolDto> getAllSchools();
    SchoolDto updateSchool(UUID id, SchoolRequest request);
    void deleteSchool(UUID id);
}