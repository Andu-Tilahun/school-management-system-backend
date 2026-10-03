package com.schoolmanagment.coreservice.exam.dto;

import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.academicyear.entity.Term;
import com.schoolmanagment.coreservice.exam.entity.StudentMark;
import com.schoolmanagment.coreservice.exam.enums.MarkStatus;
import com.schoolmanagment.coreservice.exam.enums.MarkType;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentMarkDto {

    private UUID id;

    private UUID enrollmentTermId;
    private String enrollmentTermLabel;
    private UUID studentId;
    private String studentFullName;

    private UUID subjectId;
    private String subjectName;

    private MarkType type;
    private MarkStatus status;
    private Double studMark;
    private Double totalMarkWeight;

    private Boolean active;

    public static StudentMarkDto fromEntity(StudentMark mark) {
        EnrollmentTerm enrollmentTerm = mark.getEnrollmentTerm();
        Student student = enrollmentTerm.getEnrollment().getStudent();

        return StudentMarkDto.builder()
                .id(mark.getId())
                .enrollmentTermId(enrollmentTerm.getId())
                .enrollmentTermLabel(buildTermLabel(enrollmentTerm))
                .studentId(student.getId())
                .studentFullName(buildFullName(student))
                .subjectId(mark.getSubject().getId())
                .subjectName(mark.getSubject().getSubjectName())
                .type(mark.getType())
                .status(mark.getStatus())
                .studMark(mark.getStudMark())
                .totalMarkWeight(mark.getTotalMarkWeight())
                .active(mark.getActive())
                .build();
    }

    private static String buildTermLabel(EnrollmentTerm enrollmentTerm) {
        Term term = enrollmentTerm.getTerm();
        if (term == null) {
            return null;
        }
        AcademicYear academicYear = term.getAcademicYear();
        String year = academicYear != null ? academicYear.getAcYear() : null;
        String semester = term.getSemester() == null
                ? null
                : term.getSemester().name().charAt(0)
                + term.getSemester().name().substring(1).toLowerCase().replace('_', ' ');
        if (year != null && semester != null) {
            return year + " · " + semester;
        }
        return year != null ? year : semester;
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
