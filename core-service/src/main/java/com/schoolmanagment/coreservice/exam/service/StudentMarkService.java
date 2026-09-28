package com.schoolmanagment.coreservice.exam.service;

import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import com.schoolmanagment.coreservice.teacher.dto.TeacherSubjectAssignmentDto;

import java.util.List;

public interface StudentMarkService {

    StudentMarkDto register(StudentMarkRequest request);

    List<TeacherSubjectAssignmentDto> getSubjectsForCurrentTeacher();
}
