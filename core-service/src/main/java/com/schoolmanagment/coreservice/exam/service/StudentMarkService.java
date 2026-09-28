package com.schoolmanagment.coreservice.exam.service;

import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkFilterRequest;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.teacher.dto.TeacherSubjectAssignmentDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StudentMarkService {

    StudentMarkDto register(StudentMarkRequest request);

    Page<StudentMarkDto> filter(StudentMarkFilterRequest request);

    List<TeacherSubjectAssignmentDto> getSubjectsForCurrentTeacher();

    List<TeacherSubjectAssignmentDto> getSubjectsForCurrentStudent();

    List<StudentDto> getStudentsForCurrentEmergencyContact();

    List<TeacherSubjectAssignmentDto> getSubjectsForEmergencyContactStudent(UUID studentId);
}
