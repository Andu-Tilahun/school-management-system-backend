package com.schoolmanagment.coreservice.subject.service;

import com.schoolmanagment.coreservice.subject.dto.SubjectDto;
import com.schoolmanagment.coreservice.subject.dto.SubjectRequest;

import java.util.List;
import java.util.UUID;

public interface SubjectService {
    SubjectDto createSubject(SubjectRequest request);
    SubjectDto getSubjectById(UUID id);
    List<SubjectDto> getAllSubjects();
    SubjectDto updateSubject(UUID id, SubjectRequest request);
    void deleteSubject(UUID id);


}
