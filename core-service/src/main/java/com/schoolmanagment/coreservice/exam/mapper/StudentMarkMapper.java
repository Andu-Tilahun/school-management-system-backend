package com.schoolmanagment.coreservice.exam.mapper;

import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import com.schoolmanagment.coreservice.exam.entity.StudentMark;
import com.schoolmanagment.coreservice.exam.enums.MarkStatus;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentMarkMapper {

    public StudentMarkDto toDto(StudentMark mark) {
        return StudentMarkDto.fromEntity(mark);
    }

    public List<StudentMarkDto> toDtoList(List<StudentMark> marks) {
        return marks.stream().map(this::toDto).toList();
    }


    public StudentMark toEntity(StudentMarkRequest request, EnrollmentTerm enrollmentTerm, Subject subject) {
        return StudentMark.builder()
                .enrollmentTerm(enrollmentTerm)
                .subject(subject)
                .type(request.getType())
                .status(MarkStatus.REGISTERED)
                .active(true)
                .build();
    }
}
