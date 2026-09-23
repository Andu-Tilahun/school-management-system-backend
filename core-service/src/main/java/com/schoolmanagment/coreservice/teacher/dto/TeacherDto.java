package com.schoolmanagment.coreservice.teacher.dto;

import com.schoolmanagment.coreservice.student.enums.Gender;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {

    private UUID id;
    private UUID schoolId;
    private String schoolName;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String nationality;
    private String subCity;
    private Integer kebele;
    private String houseNumber;
    private String mobileNumber;
    private List<TeacherSubjectAssignmentDto> subjectAssignments;
    private LocalDateTime createdAt;
    private String createdByName;
    private String updatedByName;

    public static TeacherDto fromEntity(Teacher teacher) {
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
}
