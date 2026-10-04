package com.schoolmanagment.coreservice.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTimetableOptionsDto {

    private UUID classSectionId;
    private List<StudentTimetableSlotDto> slots;
}
