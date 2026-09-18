package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.coreservice.student.dto.EmergencyContactDto;
import com.schoolmanagment.coreservice.student.dto.EmergencyContactRequest;
import com.schoolmanagment.coreservice.student.entity.Student;

import java.util.List;
import java.util.Optional;

public interface EmergencyContactService {

    Optional<EmergencyContactDto> findByEmail(String email);

    void syncStudentLinks(Student student, List<EmergencyContactRequest> requests);
}
