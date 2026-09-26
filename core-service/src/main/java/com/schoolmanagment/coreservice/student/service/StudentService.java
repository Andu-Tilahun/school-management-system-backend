package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.student.dto.StudentFilterRequest;
import com.schoolmanagment.coreservice.student.dto.StudentRequest;
import com.schoolmanagment.coreservice.student.entity.Student;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StudentService {

    Student findActiveStudentById(UUID id);

    Page<StudentDto> getAllStudents(int page, int size);

    Page<StudentDto> filterStudents(StudentFilterRequest request);

    StudentDto getStudentById(UUID id);

    StudentDto createStudent(StudentRequest request);

    StudentDto updateStudent(UUID id, StudentRequest request);

    void deleteStudent(UUID id);

    List<StudentDto> getStudentsByTimetable();

    List<StudentDto> getStudentsByHomeroom();

    List<StudentDto> getStudentsByClassSection(UUID classSectionId);
}
