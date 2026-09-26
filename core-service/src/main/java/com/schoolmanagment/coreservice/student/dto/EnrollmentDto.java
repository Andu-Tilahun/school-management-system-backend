package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDto {

    private UUID id;
    private UUID studentId;
    private UUID schoolId;
    private UUID classSectionId;
    private UUID gradeId;
    private String gradeName;
    private EnrollmentStatus status;
    private List<EnrollmentTermDto> enrollmentTerms;
    private LocalDateTime createdAt;

    public static EnrollmentDto fromEntity(Enrollment enrollment) {
        Student student = enrollment.getStudent();
        ClassSection classSection = enrollment.getClassSection();
        Grade grade = classSection != null ? classSection.getGrade() : null;

        return EnrollmentDto.builder()
                .id(enrollment.getId())
                .studentId(student != null ? student.getId() : null)
                .schoolId(enrollment.getSchoolId())
                .classSectionId(classSection != null ? classSection.getId() : null)
                .gradeId(grade != null ? grade.getId() : null)
                .gradeName(grade != null ? grade.getName() : null)
                .status(enrollment.getStatus())
                .enrollmentTerms(toEnrollmentTermDtos(enrollment))
                .createdAt(enrollment.getCreatedAt())
                .build();
    }

    private static List<EnrollmentTermDto> toEnrollmentTermDtos(Enrollment enrollment) {
        List<EnrollmentTerm> enrollmentTerms = enrollment.getEnrollmentTerms();
        if (enrollmentTerms == null || !Hibernate.isInitialized(enrollmentTerms)) {
            return null;
        }
        return enrollmentTerms.stream()
                .filter(enrollmentTerm -> Boolean.TRUE.equals(enrollmentTerm.getActive()))
                .map(EnrollmentTermDto::fromEntity)
                .toList();
    }
}
