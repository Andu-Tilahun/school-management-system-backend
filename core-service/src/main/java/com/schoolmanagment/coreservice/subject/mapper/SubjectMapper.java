package com.schoolmanagment.coreservice.subject.mapper;

import com.schoolmanagment.coreservice.subject.dto.SubjectDto;
import com.schoolmanagment.coreservice.subject.dto.SubjectRequest;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {
    public SubjectDto toDto(Subject subject){
        if (subject == null) return null;
        return SubjectDto.builder()
                .id(subject.getId())
                .schoolId(subject.getSchool().getId())
                .schoolName(subject.getSchool().getSchoolName())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .status(subject.getStatus())
                .build();
    }

    public Subject toEntity(SubjectRequest request){
        return Subject.builder()
                .subjectCode(request.getSubjectCode())
                .subjectName(request.getSubjectName())
                .status(SubjectStatus.ACTIVE)
                .build();
    }

    public void updateEntity(Subject subject, SubjectRequest request) {
        subject.setSubjectCode(request.getSubjectCode());
        subject.setSubjectName(request.getSubjectName());
    }
}
