package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.student.enums.Gender;
import com.schoolmanagment.coreservice.student.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {

    private UUID id;
    private UUID schoolId;
    private String schoolName;
    private String firstName;
    private String middleName;
    private String lastName;
    private int age;
    private Gender gender;
    private String nationality;
    private String subCity;
    private Integer kebele;
    private String houseNumber;
    private String mobileNumber;
    private LocalDateTime createdAt;

    public static StudentDto fromEntity(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .schoolId(student.getSchoolId())
                .firstName(student.getFirstName())
                .middleName(student.getMiddleName())
                .lastName(student.getLastName())
                .age(student.getAge())
                .gender(student.getGender())
                .nationality(student.getNationality())
                .subCity(student.getSubCity())
                .kebele(student.getKebele())
                .houseNumber(student.getHouseNumber())
                .mobileNumber(student.getMobileNumber())
                .createdAt(student.getCreatedAt())
                .build();
    }
}
