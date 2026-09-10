package com.schoolmanagment.coreservice.academicyear.mapper;

import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearDto;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearRequest;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import org.springframework.stereotype.Component;

@Component
public class AcademicYearMapper {

    public AcademicYearDto toDto(AcademicYear academicYear) {
        return AcademicYearDto.builder()
                .id(academicYear.getId())
                .schoolId(academicYear.getSchoolId())
                .acYear(academicYear.getAcYear())
                .semester(academicYear.getSemester())
                .active(academicYear.getActive())
                .createdAt(academicYear.getCreatedAt())
                .build();
    }

    public AcademicYear toEntity(AcademicYearRequest request) {
        return AcademicYear.builder()
                .acYear(request.getAcYear())
                .semester(request.getSemester())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    public void updateEntity(AcademicYear academicYear, AcademicYearRequest request) {
        academicYear.setAcYear(request.getAcYear());
        academicYear.setSemester(request.getSemester());
        if (request.getActive() != null) {
            academicYear.setActive(request.getActive());
        }
    }
}
