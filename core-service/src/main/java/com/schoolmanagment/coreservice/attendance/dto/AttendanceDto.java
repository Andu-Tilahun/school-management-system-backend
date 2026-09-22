package com.schoolmanagment.coreservice.attendance.dto;

import com.schoolmanagment.coreservice.attendance.entity.Attendance;
import com.schoolmanagment.coreservice.attendance.enums.AttendanceStatus;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {

    private UUID id;
    private UUID schoolId;
    private UUID enrollmentId;
    private UUID studentId;
    private String studentFirstName;
    private String studentLastName;
    private UUID classSectionId;
    private PenaltyTrigger penaltyTrigger;
    private SourceModule sourceModule;
    private LocalDate dateOccurred;
    private AttendanceStatus status;
    private String remark;
    private LocalDateTime createdAt;

    public static AttendanceDto fromEntity(Attendance attendance) {
        Enrollment enrollment = attendance.getEnrollment();
        Student student = enrollment != null ? enrollment.getStudent() : null;
        ClassSection classSection = enrollment != null ? enrollment.getClassSection() : null;
        PenaltyTrigger penaltyTrigger = attendance.getPenaltyTrigger();

        return AttendanceDto.builder()
                .id(attendance.getId())
                .schoolId(attendance.getSchoolId())
                .enrollmentId(enrollment != null ? enrollment.getId() : null)
                .studentId(student != null ? student.getId() : null)
                .studentFirstName(student != null ? student.getFirstName() : null)
                .studentLastName(student != null ? student.getLastName() : null)
                .classSectionId(classSection != null ? classSection.getId() : null)
                .penaltyTrigger(penaltyTrigger)
                .sourceModule(penaltyTrigger != null ? penaltyTrigger.getSourceModule() : null)
                .dateOccurred(attendance.getDateOccurred())
                .status(attendance.getStatus())
                .remark(attendance.getRemark())
                .createdAt(attendance.getCreatedAt())
                .build();
    }
}
