package com.schoolmanagment.coreservice.classsection.service;

import com.schoolmanagment.coreservice.classsection.dto.ClassSectionHomeroomDto;

import java.util.List;
import java.util.UUID;

public interface ClassSectionHomeroomService {

    ClassSectionHomeroomDto reassign(UUID classSectionId, UUID newTeacherId);

    ClassSectionHomeroomDto getCurrent(UUID classSectionId);

    List<ClassSectionHomeroomDto> getHistory(UUID classSectionId);

    List<ClassSectionHomeroomDto> getByTeacher(UUID teacherId);
}
