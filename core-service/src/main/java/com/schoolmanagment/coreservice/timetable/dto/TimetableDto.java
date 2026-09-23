package com.schoolmanagment.coreservice.timetable.dto;

import com.schoolmanagment.coreservice.timetable.enums.Day;
import com.schoolmanagment.coreservice.timetable.enums.Period;
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
public class TimetableDto {

    private UUID id;
    private UUID schoolId;
    private UUID classSectionId;
    private String classSectionName;
    private UUID subjectId;
    private String subjectName;
    private String subjectCode;
    private UUID teacherId;
    private String teacherFullName;
    private Day day;
    private Period period;
    private LocalDateTime createdAt;
    private String createdByName;
    private String updatedByName;
}
