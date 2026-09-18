package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.coreservice.student.dto.EmergencyContactDto;
import com.schoolmanagment.coreservice.student.dto.EmergencyContactRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmergencyContactService {

    Optional<EmergencyContactDto> findByEmail(String email);

    EmergencyContactDto registerEmergencyContact(UUID studentId, EmergencyContactRequest request);

    List<EmergencyContactDto> getEmergencyContactsForStudent(UUID studentId);

    void removeEmergencyContactFromStudent(UUID studentId, UUID emergencyContactId);

    EmergencyContactDto updateEmergencyContact(UUID emergencyContactId, EmergencyContactRequest request);
}
