package com.schoolmanagment.coreservice.offencerecord.dto;

import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.offencerecord.entity.OffenceRecord;
import com.schoolmanagment.coreservice.offencerecord.enums.OffenceRecordStatus;
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
public class OffenceRecordDto {

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
    private OffenceRecordStatus status;
    private String remark;
    private LocalDateTime createdAt;

    public static OffenceRecordDto fromEntity(OffenceRecord offenceRecord) {
        Enrollment enrollment = offenceRecord.getEnrollment();
        Student student = enrollment != null ? enrollment.getStudent() : null;
        ClassSection classSection = enrollment != null ? enrollment.getClassSection() : null;
        PenaltyTrigger penaltyTrigger = offenceRecord.getPenaltyTrigger();

        return OffenceRecordDto.builder()
                .id(offenceRecord.getId())
                .schoolId(offenceRecord.getSchoolId())
                .enrollmentId(enrollment != null ? enrollment.getId() : null)
                .studentId(student != null ? student.getId() : null)
                .studentFirstName(student != null ? student.getFirstName() : null)
                .studentLastName(student != null ? student.getLastName() : null)
                .classSectionId(classSection != null ? classSection.getId() : null)
                .penaltyTrigger(penaltyTrigger)
                .sourceModule(penaltyTrigger != null ? penaltyTrigger.getSourceModule() : null)
                .dateOccurred(offenceRecord.getDateOccurred())
                .status(offenceRecord.getStatus())
                .remark(offenceRecord.getRemark())
                .createdAt(offenceRecord.getCreatedAt())
                .build();
    }
}
