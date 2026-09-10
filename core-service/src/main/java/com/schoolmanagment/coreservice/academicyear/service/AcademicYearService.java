package com.schoolmanagment.coreservice.academicyear.service;

import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearDto;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearFilterRequest;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AcademicYearService {

    Page<AcademicYearDto> getAllAcademicYears(int page, int size);

    Page<AcademicYearDto> filterAcademicYears(AcademicYearFilterRequest request);

    AcademicYearDto getAcademicYearById(UUID id);

    AcademicYearDto createAcademicYear(AcademicYearRequest request);

    AcademicYearDto updateAcademicYear(UUID id, AcademicYearRequest request);

    void deleteAcademicYear(UUID id);
}
