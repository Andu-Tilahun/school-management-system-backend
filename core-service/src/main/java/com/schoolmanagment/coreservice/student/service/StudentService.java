package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.student.dto.StudentFilterRequest;
import com.schoolmanagment.coreservice.student.dto.StudentRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface StudentService {

    Page<StudentDto> getAllStudents(int page, int size);

    Page<StudentDto> filterStudents(StudentFilterRequest request);

    StudentDto getStudentById(UUID id);

    StudentDto createStudent(StudentRequest request);

    StudentDto updateStudent(UUID id, StudentRequest request);

    void deleteStudent(UUID id);
}
