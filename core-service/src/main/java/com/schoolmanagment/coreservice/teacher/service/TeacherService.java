package com.schoolmanagment.coreservice.teacher.service;

import com.schoolmanagment.coreservice.teacher.dto.TeacherDto;
import com.schoolmanagment.coreservice.teacher.dto.TeacherFilterRequest;
import com.schoolmanagment.coreservice.teacher.dto.TeacherRequest;
import com.schoolmanagment.coreservice.teacher.entity.TeacherSubjectAssignment;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TeacherService {

    TeacherSubjectAssignment findActiveTeacherSubjectAssignmentById(UUID id);

    Page<TeacherDto> getAllTeachers(int page, int size);

    Page<TeacherDto> filterTeachers(TeacherFilterRequest request);

    TeacherDto getTeacherById(UUID id);

    TeacherDto createTeacher(TeacherRequest request);

    TeacherDto updateTeacher(UUID id, TeacherRequest request);

    void deleteTeacher(UUID id);
}
