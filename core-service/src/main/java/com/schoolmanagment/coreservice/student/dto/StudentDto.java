package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    private LocalDate birthDate;
    private Gender gender;
    private String nationality;
    private String subCity;
    private Integer kebele;
    private String houseNumber;
    private String mobileNumber;
    private List<EmergencyContactDto> emergencyContacts;
    private LocalDateTime createdAt;
    private String createdByName;
    private String updatedByName;

    public static StudentDto fromEntity(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .schoolId(student.getSchoolId())
                .firstName(student.getFirstName())
                .middleName(student.getMiddleName())
                .lastName(student.getLastName())
                .birthDate(student.getBirthDate())
                .gender(student.getGender())
                .nationality(student.getNationality())
                .subCity(student.getSubCity())
                .kebele(student.getKebele())
                .houseNumber(student.getHouseNumber())
                .mobileNumber(student.getMobileNumber())
                .emergencyContacts(toEmergencyContactDtos(student))
                .createdAt(student.getCreatedAt())
                .createdByName(student.getCreatedByName())
                .updatedByName(student.getUpdatedByName())
                .build();
    }

    private static List<EmergencyContactDto> toEmergencyContactDtos(Student student) {
        List<EmergencyContact> contacts = student.getEmergencyContacts();
        if (contacts == null || !Hibernate.isInitialized(contacts)) {
            return null;
        }
        return contacts.stream()
                .filter(contact -> Boolean.TRUE.equals(contact.getActive()))
                .map(EmergencyContactDto::fromEntity)
                .toList();
    }
}
