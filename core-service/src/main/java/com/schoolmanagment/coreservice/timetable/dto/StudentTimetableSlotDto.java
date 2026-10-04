package com.schoolmanagment.coreservice.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTimetableSlotDto {

    private UUID subjectId;
    private String subjectCode;
    private String subjectName;
    private UUID teacherId;
    private String teacherFirstName;
    private String teacherMiddleName;
    private String teacherLastName;
}
