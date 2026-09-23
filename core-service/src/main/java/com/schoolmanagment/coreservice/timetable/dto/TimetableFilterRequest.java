package com.schoolmanagment.coreservice.timetable.dto;

import com.schoolmanagment.coreservice.timetable.enums.Day;
import com.schoolmanagment.coreservice.timetable.enums.Period;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableFilterRequest {

    private UUID schoolId;

    private UUID classSectionId;

    private UUID teacherSubjectAssignmentId;

    private UUID subjectId;

    private UUID teacherId;

    private Day day;

    private Period period;

    private String searchText;

    private String sortBy;

    private String sortDirection = "ASC";

    private int page = 0;

    private int size = 10;
}
