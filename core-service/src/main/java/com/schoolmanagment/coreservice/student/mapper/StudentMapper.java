package com.schoolmanagment.coreservice.student.mapper;

import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.student.dto.StudentRequest;
import com.schoolmanagment.coreservice.student.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentDto toDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .middleName(student.getMiddleName())
                .lastName(student.getLastName())
                .age(student.getAge())
                .sex(student.getSex())
                .nationality(student.getNationality())
                .subCity(student.getSubCity())
                .kebele(student.getKebele())
                .houseNumber(student.getHouseNumber())
                .mobileNumber(student.getMobileNumber())
                .createdAt(student.getCreatedAt())
                .build();
    }

    public Student toEntity(StudentRequest request) {
        return Student.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .age(request.getAge())
                .sex(request.getSex())
                .nationality(request.getNationality())
                .subCity(request.getSubCity())
                .kebele(request.getKebele())
                .houseNumber(request.getHouseNumber())
                .mobileNumber(request.getMobileNumber())
                .active(true)
                .build();
    }

    public void updateEntity(Student student, StudentRequest request) {
        student.setFirstName(request.getFirstName());
        student.setMiddleName(request.getMiddleName());
        student.setLastName(request.getLastName());
        student.setAge(request.getAge());
        student.setSex(request.getSex());
        student.setNationality(request.getNationality());
        student.setSubCity(request.getSubCity());
        student.setKebele(request.getKebele());
        student.setHouseNumber(request.getHouseNumber());
        student.setMobileNumber(request.getMobileNumber());
    }
}
