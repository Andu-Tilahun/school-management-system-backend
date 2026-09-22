package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.entity.Term;
import com.schoolmanagment.coreservice.academicyear.enums.Semester;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentTermDto {

    private UUID id;

    private UUID enrollmentId;
    private UUID studentId;
    private String studentFullName;

    private UUID termId;
    private Semester semester;
    private Integer termSequenceOrder;

    private UUID academicYearId;
    private String academicYear;

    private EnrollmentTermStatus status;

    private LocalDate registeredAt;

    private Boolean active;

    public static EnrollmentTermDto fromEntity(EnrollmentTerm enrollmentTerm) {
        Student student = enrollmentTerm.getEnrollment().getStudent();
        Term term = enrollmentTerm.getTerm();
        AcademicYear academicYear = term.getAcademicYear();

        return EnrollmentTermDto.builder()
                .id(enrollmentTerm.getId())
                .enrollmentId(enrollmentTerm.getEnrollment().getId())
                .studentId(student.getId())
                .studentFullName(buildFullName(student))
                .termId(term.getId())
                .semester(term.getSemester())
                .academicYearId(academicYear.getId())
                .academicYear(academicYear.getAcYear())
                .status(enrollmentTerm.getStatus())
                .registeredAt(enrollmentTerm.getRegisteredAt())
                .active(enrollmentTerm.getActive())
                .build();
    }

    private static String buildFullName(Student student) {
        StringBuilder sb = new StringBuilder(student.getFirstName());
        if (student.getMiddleName() != null && !student.getMiddleName().isBlank()) {
            sb.append(" ").append(student.getMiddleName());
        }
        sb.append(" ").append(student.getLastName());
        return sb.toString();
    }
}
