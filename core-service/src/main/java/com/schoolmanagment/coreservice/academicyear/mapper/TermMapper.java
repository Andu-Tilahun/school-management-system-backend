package com.schoolmanagment.coreservice.academicyear.mapper;

import com.schoolmanagment.coreservice.academicyear.dto.TermDto;
import com.schoolmanagment.coreservice.academicyear.dto.TermRequest;
import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.entity.Term;
import org.springframework.stereotype.Component;

@Component
public class TermMapper {

    public TermDto toDto(Term term) {
        return TermDto.fromEntity(term);
    }

    public Term toEntity(TermRequest request, AcademicYear academicYear) {
        return Term.builder()
                .academicYear(academicYear)
                .semester(request.getSemester())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    public void updateEntity(Term term, TermRequest request, AcademicYear academicYear) {
        term.setAcademicYear(academicYear);
        term.setSemester(request.getSemester());
        term.setStartDate(request.getStartDate());
        term.setEndDate(request.getEndDate());
        if (request.getActive() != null) {
            term.setActive(request.getActive());
        }
    }
}
