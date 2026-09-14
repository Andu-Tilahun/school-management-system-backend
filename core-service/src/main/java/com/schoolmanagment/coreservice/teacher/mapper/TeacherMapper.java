package com.schoolmanagment.coreservice.teacher.mapper;

import com.schoolmanagment.coreservice.teacher.dto.TeacherDto;
import com.schoolmanagment.coreservice.teacher.dto.TeacherRequest;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import org.springframework.stereotype.Component;

@Component
public class TeacherMapper {

    public TeacherDto toDto(Teacher teacher) {
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
                .createdAt(teacher.getCreatedAt())
                .createdByName(teacher.getCreatedByName())
                .updatedByName(teacher.getUpdatedByName())
                .build();
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
