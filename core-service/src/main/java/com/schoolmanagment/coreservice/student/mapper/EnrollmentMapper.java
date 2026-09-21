package com.schoolmanagment.coreservice.student.mapper;

import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.student.dto.EnrollmentDto;
import com.schoolmanagment.coreservice.student.dto.EnrollmentRequest;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {

    public EnrollmentDto toDto(Enrollment enrollment) {
        return EnrollmentDto.fromEntity(enrollment);
    }

    public Enrollment toEntity(
            EnrollmentRequest request,
            Student student,
            ClassSection classSection,
            AcademicYear academicYear
    ) {
        return Enrollment.builder()
                .student(student)
                .classSection(classSection)
                .academicYear(academicYear)
                .status(request.getStatus() != null ? request.getStatus() : EnrollmentStatus.ACTIVE)
                .active(true)
                .build();
    }

    public void updateEntity(
            Enrollment enrollment,
            EnrollmentRequest request,
            ClassSection classSection,
            AcademicYear academicYear
    ) {
        enrollment.setClassSection(classSection);
        enrollment.setAcademicYear(academicYear);
        if (request.getStatus() != null) {
            enrollment.setStatus(request.getStatus());
        }
    }
}
