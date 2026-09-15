package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactDto {

    private UUID id;
    private UUID studentId;
    private UUID schoolId;
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
    private String email;
    private LocalDateTime createdAt;

    public static EmergencyContactDto fromEntity(EmergencyContact contact) {
        Student student = contact.getStudent();
        return EmergencyContactDto.builder()
                .id(contact.getId())
                .studentId(student != null ? student.getId() : null)
                .schoolId(contact.getSchoolId())
                .firstName(contact.getFirstName())
                .middleName(contact.getMiddleName())
                .lastName(contact.getLastName())
                .birthDate(contact.getBirthDate())
                .gender(contact.getGender())
                .nationality(contact.getNationality())
                .subCity(contact.getSubCity())
                .kebele(contact.getKebele())
                .houseNumber(contact.getHouseNumber())
                .mobileNumber(contact.getMobileNumber())
                .email(contact.getEmail())
                .createdAt(contact.getCreatedAt())
                .build();
    }
}
