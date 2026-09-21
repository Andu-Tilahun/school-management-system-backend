package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private UUID academicYearId;
    private String acYear;
    private String semester;
    private EnrollmentStatus status;
    private LocalDateTime createdAt;

    public static EnrollmentDto fromEntity(Enrollment enrollment) {
        Student student = enrollment.getStudent();
        ClassSection classSection = enrollment.getClassSection();
        Grade grade = classSection != null ? classSection.getGrade() : null;
        AcademicYear academicYear = enrollment.getAcademicYear();

        return EnrollmentDto.builder()
                .id(enrollment.getId())
                .studentId(student != null ? student.getId() : null)
                .schoolId(enrollment.getSchoolId())
                .classSectionId(classSection != null ? classSection.getId() : null)
                .gradeId(grade != null ? grade.getId() : null)
                .gradeName(grade != null ? grade.getName() : null)
                .academicYearId(academicYear != null ? academicYear.getId() : null)
                .acYear(academicYear != null ? academicYear.getAcYear() : null)
                .semester(academicYear != null ? academicYear.getSemester() : null)
                .status(enrollment.getStatus())
                .createdAt(enrollment.getCreatedAt())
                .build();
    }
}
