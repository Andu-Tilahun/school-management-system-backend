package com.schoolmanagment.coreservice.teacher.service;

import com.schoolmanagment.coreservice.teacher.dto.TeacherDto;
import com.schoolmanagment.coreservice.teacher.dto.TeacherFilterRequest;
import com.schoolmanagment.coreservice.teacher.dto.TeacherRequest;
import com.schoolmanagment.coreservice.teacher.dto.TeacherSubjectAssignmentDto;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.teacher.entity.TeacherSubjectAssignment;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface TeacherService {

    Teacher findActiveTeacherById(UUID id);

    TeacherSubjectAssignment findActiveTeacherSubjectAssignmentById(UUID id);

    List<TeacherSubjectAssignmentDto> getAssignedSubjects();

    List<TeacherDto> getTeachersBySubject(UUID subjectId);

    List<TeacherSubjectAssignmentDto> getSubjectsForCurrentStudent();

    List<TeacherSubjectAssignmentDto> getSubjectsByStudentId(UUID studentId);

    Page<TeacherDto> getAllTeachers(int page, int size);

    Page<TeacherDto> filterTeachers(TeacherFilterRequest request);

    TeacherDto getTeacherById(UUID id);

    TeacherDto createTeacher(TeacherRequest request);

    TeacherDto createTeacherAccount(UUID id);

    TeacherDto updateTeacher(UUID id, TeacherRequest request);

    void deleteTeacher(UUID id);
}
