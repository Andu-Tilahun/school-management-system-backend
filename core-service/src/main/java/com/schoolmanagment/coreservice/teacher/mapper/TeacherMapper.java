package com.schoolmanagment.coreservice.teacher.mapper;

import com.schoolmanagment.coreservice.teacher.dto.TeacherDto;
import com.schoolmanagment.coreservice.teacher.dto.TeacherRequest;
import com.schoolmanagment.coreservice.teacher.dto.TeacherSubjectAssignmentDto;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.teacher.entity.TeacherSubjectAssignment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeacherMapper {

    public TeacherDto toDto(Teacher teacher, List<TeacherSubjectAssignment> assignments) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .schoolId(teacher.getSchoolId())
                .firstName(teacher.getFirstName())
                .middleName(teacher.getMiddleName())
                .lastName(teacher.getLastName())
                .birthDate(teacher.getBirthDate())
                .gender(teacher.getGender())
                .subCity(teacher.getSubCity())
                .kebele(teacher.getKebele())
                .houseNumber(teacher.getHouseNumber())
                .mobileNumber(teacher.getMobileNumber())
                .subjectAssignments(toDto(assignments))
                .createdAt(teacher.getCreatedAt())
                .createdByName(teacher.getCreatedByName())
                .updatedByName(teacher.getUpdatedByName())
                .build();
    }

    private List<TeacherSubjectAssignmentDto> toDto(List<TeacherSubjectAssignment> assignments) {
        return assignments.stream()
                .map(assignment -> TeacherSubjectAssignmentDto.builder()
                        .id(assignment.getId())
                        .subjectId(assignment.getSubject().getId())
                        .subjectCode(assignment.getSubject().getSubjectCode())
                        .subjectName(assignment.getSubject().getSubjectName())
                        .active(assignment.getActive())
                        .build())
                .toList();
    }

    public Teacher toEntity(TeacherRequest request) {
        return Teacher.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .subCity(request.getSubCity())
                .kebele(request.getKebele())
                .houseNumber(request.getHouseNumber())
                .mobileNumber(request.getMobileNumber())
                .active(true)
                .build();
    }

    public void updateEntity(Teacher teacher, TeacherRequest request) {
        teacher.setFirstName(request.getFirstName());
        teacher.setMiddleName(request.getMiddleName());
        teacher.setLastName(request.getLastName());
        teacher.setBirthDate(request.getBirthDate());
        teacher.setGender(request.getGender());
        teacher.setSubCity(request.getSubCity());
        teacher.setKebele(request.getKebele());
        teacher.setHouseNumber(request.getHouseNumber());
        teacher.setMobileNumber(request.getMobileNumber());
    }
}
