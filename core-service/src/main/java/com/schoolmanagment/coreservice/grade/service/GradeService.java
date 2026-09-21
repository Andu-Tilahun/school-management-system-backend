package com.schoolmanagment.coreservice.grade.service;

import com.schoolmanagment.coreservice.grade.dto.GradeDto;
import com.schoolmanagment.coreservice.grade.dto.GradeFilterRequest;
import com.schoolmanagment.coreservice.grade.dto.GradeRequest;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface GradeService {

    Grade findActiveGradeById(UUID id);

    Page<GradeDto> getAllGrades(int page, int size);

    Page<GradeDto> filterGrades(GradeFilterRequest request);

    GradeDto getGradeById(UUID id);

    GradeDto createGrade(GradeRequest request);

    GradeDto updateGrade(UUID id, GradeRequest request);

    void deleteGrade(UUID id);
}
