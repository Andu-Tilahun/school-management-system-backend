package com.schoolmanagment.coreservice.exam.service;

import com.schoolmanagment.coreservice.exam.dto.GradeStudentMarkRequest;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkFilterRequest;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface StudentMarkService {

    Page<StudentMarkDto> list(int page, int size);

    Page<StudentMarkDto> filter(StudentMarkFilterRequest request);

    StudentMarkDto getById(UUID id);

    StudentMarkDto register(StudentMarkRequest request);

    StudentMarkDto grade(UUID id, GradeStudentMarkRequest request);

    void delete(UUID id);
}
