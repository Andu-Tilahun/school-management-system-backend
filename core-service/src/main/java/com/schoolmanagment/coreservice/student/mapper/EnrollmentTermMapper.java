package com.schoolmanagment.coreservice.student.mapper;

import com.schoolmanagment.coreservice.academicyear.entity.Term;
import com.schoolmanagment.coreservice.student.dto.EnrollmentTermDto;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EnrollmentTermMapper {

    public EnrollmentTermDto toDto(EnrollmentTerm enrollmentTerm) {
        return EnrollmentTermDto.fromEntity(enrollmentTerm);
    }

    public List<EnrollmentTermDto> toDtoList(List<EnrollmentTerm> enrollmentTerms) {
        return enrollmentTerms.stream()
                .map(this::toDto)
                .toList();
    }

    public EnrollmentTerm toEntity(Enrollment enrollment, Term term) {
        return EnrollmentTerm.builder()
                .enrollment(enrollment)
                .term(term)
                .status(EnrollmentTermStatus.ACTIVE)
                .registeredAt(LocalDate.now())
                .active(true)
                .build();
    }


    public void updateStatus(EnrollmentTerm enrollmentTerm, EnrollmentTermStatus newStatus) {
        if (newStatus != null) {
            enrollmentTerm.setStatus(newStatus);
        }
    }
}
