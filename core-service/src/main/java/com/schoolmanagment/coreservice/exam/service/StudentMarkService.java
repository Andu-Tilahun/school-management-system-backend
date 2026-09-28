package com.schoolmanagment.coreservice.exam.service;

import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkFilterRequest;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import org.springframework.data.domain.Page;

public interface StudentMarkService {

    StudentMarkDto register(StudentMarkRequest request);

    Page<StudentMarkDto> filter(StudentMarkFilterRequest request);
}
