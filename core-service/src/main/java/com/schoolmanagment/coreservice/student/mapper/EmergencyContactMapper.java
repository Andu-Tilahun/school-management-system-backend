package com.schoolmanagment.coreservice.student.mapper;

import com.schoolmanagment.coreservice.student.dto.EmergencyContactDto;
import com.schoolmanagment.coreservice.student.dto.EmergencyContactRequest;
import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import org.springframework.stereotype.Component;

@Component
public class EmergencyContactMapper {

    public EmergencyContactDto toDto(EmergencyContact contact) {
        return EmergencyContactDto.fromEntity(contact);
    }

    public EmergencyContact toEntity(EmergencyContactRequest request) {
        return EmergencyContact.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .nationality(request.getNationality())
                .subCity(request.getSubCity())
                .kebele(request.getKebele())
                .houseNumber(request.getHouseNumber())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .active(true)
                .build();
    }
}
