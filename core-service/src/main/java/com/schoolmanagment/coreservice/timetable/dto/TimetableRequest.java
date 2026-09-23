package com.schoolmanagment.coreservice.timetable.dto;

import com.schoolmanagment.coreservice.timetable.enums.Day;
import com.schoolmanagment.coreservice.timetable.enums.Period;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableRequest {

    @NotNull(message = "Class section is required")
    private UUID classSectionId;

    @NotNull(message = "Teacher subject assignment is required")
    private UUID teacherSubjectAssignmentId;

    @NotNull(message = "Day is required")
    private Day day;

    @NotNull(message = "Period is required")
    private Period period;
}
