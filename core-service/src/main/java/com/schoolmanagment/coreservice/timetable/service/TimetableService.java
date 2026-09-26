package com.schoolmanagment.coreservice.timetable.service;

import com.schoolmanagment.coreservice.classsection.dto.ClassSectionDto;
import com.schoolmanagment.coreservice.timetable.dto.TimetableDto;
import com.schoolmanagment.coreservice.timetable.dto.TimetableFilterRequest;
import com.schoolmanagment.coreservice.timetable.dto.TimetableRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface TimetableService {

    Page<TimetableDto> getAllTimetables(int page, int size);

    Page<TimetableDto> filterTimetables(TimetableFilterRequest request);

    TimetableDto getTimetableById(UUID id);

    TimetableDto createTimetable(TimetableRequest request);

    TimetableDto updateTimetable(UUID id, TimetableRequest request);

    void deleteTimetable(UUID id);

    List<ClassSectionDto> getSectionsByTeacher(UUID teacherId);

    List<ClassSectionDto> getSectionsForCurrentTeacher();
}
