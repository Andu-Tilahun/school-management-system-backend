package com.schoolmanagment.coreservice.exam.dto;

import com.schoolmanagment.coreservice.exam.entity.StudentMark;
import com.schoolmanagment.coreservice.exam.enums.MarkStatus;
import com.schoolmanagment.coreservice.exam.enums.MarkType;
import com.schoolmanagment.coreservice.student.entity.Student;
import jakarta.validation.constraints.NotNull;
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
    private UUID studentId;
    private String studentFullName;

    private UUID subjectId;
    private String subjectName;

    private MarkType type;
    private MarkStatus status;
    private Double studMark;

    private Boolean active;

    public static StudentMarkDto fromEntity(StudentMark mark) {
        Student student = mark.getEnrollmentTerm().getEnrollment().getStudent();

        return StudentMarkDto.builder()
                .id(mark.getId())
                .enrollmentTermId(mark.getEnrollmentTerm().getId())
                .studentId(student.getId())
                .studentFullName(buildFullName(student))
                .subjectId(mark.getSubject().getId())
                .subjectName(mark.getSubject().getSubjectName())
                .type(mark.getType())
                .status(mark.getStatus())
                .studMark(mark.getStudMark())
                .active(mark.getActive())
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
