package com.schoolmanagment.coreservice.student.dto;

import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import com.schoolmanagment.coreservice.student.entity.StudentEmergencyContact;
import com.schoolmanagment.coreservice.student.enums.ContactRelationship;
import com.schoolmanagment.coreservice.student.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactDto {


    private UUID linkId;
    private UUID id;
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
    private ContactRelationship relationship;
    private Boolean isPrimary;
    private Boolean hasAccount;

    public static EmergencyContactDto fromEntity(EmergencyContact contact) {
        return EmergencyContactDto.builder()
                .id(contact.getId())
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
                .hasAccount(contact.getHasAccount())
                .build();
    }

    public static EmergencyContactDto fromStudentEmergencyContact(StudentEmergencyContact studentEmergencyContact) {
        EmergencyContactDto dto = fromEntity(studentEmergencyContact.getEmergencyContact());
        dto.setLinkId(studentEmergencyContact.getId());
        dto.setRelationship(studentEmergencyContact.getRelationship());
        dto.setIsPrimary(studentEmergencyContact.getIsPrimary());
        return dto;
    }
}
