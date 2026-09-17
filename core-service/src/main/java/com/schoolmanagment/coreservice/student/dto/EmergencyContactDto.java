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

    // Null when this DTO represents a bare person lookup.
    private ContactRelationship relationship;
    private Boolean isPrimary;

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
                .build();
    }

    public static EmergencyContactDto fromLink(StudentEmergencyContact link) {
        EmergencyContactDto dto = fromEntity(link.getEmergencyContact());
        dto.setLinkId(link.getId());
        dto.setRelationship(link.getRelationship());
        dto.setIsPrimary(link.getIsPrimary());
        return dto;
    }
}
