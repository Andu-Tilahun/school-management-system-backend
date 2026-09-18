package com.schoolmanagment.coreservice.student.mapper;

import com.schoolmanagment.coreservice.student.dto.EmergencyContactRequest;
import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import org.springframework.stereotype.Component;

@Component
public class EmergencyContactMapper {

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

    public void updateEntity(EmergencyContact contact, EmergencyContactRequest request) {
        contact.setFirstName(request.getFirstName());
        contact.setMiddleName(request.getMiddleName());
        contact.setLastName(request.getLastName());
        contact.setBirthDate(request.getBirthDate());
        contact.setGender(request.getGender());
        contact.setNationality(request.getNationality());
        contact.setSubCity(request.getSubCity());
        contact.setKebele(request.getKebele());
        contact.setHouseNumber(request.getHouseNumber());
        contact.setMobileNumber(request.getMobileNumber());
        contact.setEmail(request.getEmail());
    }
}
