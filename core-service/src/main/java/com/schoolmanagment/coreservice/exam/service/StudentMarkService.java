package com.schoolmanagment.coreservice.exam.service;

import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;

public interface StudentMarkService {

    StudentMarkDto register(StudentMarkRequest request);
}
