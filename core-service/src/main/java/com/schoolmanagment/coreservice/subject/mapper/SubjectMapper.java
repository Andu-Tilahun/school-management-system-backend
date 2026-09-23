package com.schoolmanagment.coreservice.subject.mapper;

import com.schoolmanagment.coreservice.grade.entity.Grade;
import com.schoolmanagment.coreservice.subject.dto.SubjectDto;
import com.schoolmanagment.coreservice.subject.dto.SubjectRequest;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {
    public SubjectDto toDto(Subject subject){
        if (subject == null) return null;
        Grade grade = subject.getGrade();
        return SubjectDto.builder()
                .id(subject.getId())
                .schoolId(subject.getSchoolId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .gradeId(grade != null ? grade.getId() : null)
                .gradeName(grade != null ? grade.getName() : null)
                .status(subject.getStatus())
                .createdByName(subject.getCreatedByName())
                .updatedByName(subject.getUpdatedByName())
                .build();
    }

    public Subject toEntity(SubjectRequest request, Grade grade){
        return Subject.builder()
                .subjectCode(request.getSubjectCode())
                .subjectName(request.getSubjectName())
                .grade(grade)
                .status(SubjectStatus.ACTIVE)
                .build();
    }

    public void updateEntity(Subject subject, SubjectRequest request, Grade grade) {
        subject.setSubjectCode(request.getSubjectCode());
        subject.setSubjectName(request.getSubjectName());
        subject.setGrade(grade);
    }
}
