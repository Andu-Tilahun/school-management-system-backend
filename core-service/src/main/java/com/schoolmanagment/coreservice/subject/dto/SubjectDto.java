package com.schoolmanagment.coreservice.subject.dto;

import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    private UUID id;
    private String subjectCode;
    private String subjectName;
    private Integer creditHours;
    private Integer gradeLevel;
    private SubjectStatus status;
}
