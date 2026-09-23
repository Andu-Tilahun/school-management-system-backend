package com.schoolmanagment.coreservice.subject.service;

import com.schoolmanagment.coreservice.subject.dto.SubjectDto;
import com.schoolmanagment.coreservice.subject.dto.SubjectFilterRequest;
import com.schoolmanagment.coreservice.subject.dto.SubjectRequest;
import com.schoolmanagment.coreservice.subject.entity.Subject;

import java.util.List;
import java.util.UUID;

public interface SubjectService {

    Subject findActiveSubjectById(UUID id);

    SubjectDto createSubject(SubjectRequest request);
    SubjectDto getSubjectById(UUID id);
    List<SubjectDto> getSubjects(SubjectFilterRequest filterRequest);
    SubjectDto updateSubject(UUID id, SubjectRequest request);
    void deleteSubject(UUID id);


}
