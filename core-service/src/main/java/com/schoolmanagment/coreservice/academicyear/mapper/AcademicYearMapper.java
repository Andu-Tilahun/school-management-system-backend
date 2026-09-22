package com.schoolmanagment.coreservice.academicyear.mapper;

import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearDto;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearRequest;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import org.springframework.stereotype.Component;

@Component
public class AcademicYearMapper {

    public AcademicYearDto toDto(AcademicYear academicYear) {
        return AcademicYearDto.fromEntity(academicYear);
    }

    public AcademicYear toEntity(AcademicYearRequest request) {
        return AcademicYear.builder()
                .acYear(request.getAcYear())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    public void updateEntity(AcademicYear academicYear, AcademicYearRequest request) {
        academicYear.setAcYear(request.getAcYear());
        academicYear.setStartDate(request.getStartDate());
        academicYear.setEndDate(request.getEndDate());
        if (request.getActive() != null) {
            academicYear.setActive(request.getActive());
        }
    }
}
