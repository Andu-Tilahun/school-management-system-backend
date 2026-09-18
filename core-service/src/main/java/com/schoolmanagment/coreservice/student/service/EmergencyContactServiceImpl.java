package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.student.dto.EmergencyContactDto;
import com.schoolmanagment.coreservice.student.dto.EmergencyContactRequest;
import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.entity.StudentEmergencyContact;
import com.schoolmanagment.coreservice.student.mapper.EmergencyContactMapper;
import com.schoolmanagment.coreservice.student.repository.EmergencyContactRepository;
import com.schoolmanagment.coreservice.student.repository.StudentEmergencyContactRepository;
import com.schoolmanagment.coreservice.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyContactServiceImpl implements EmergencyContactService {

    private final StudentRepository studentRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final StudentEmergencyContactRepository studentEmergencyContactRepository;
    private final EmergencyContactMapper emergencyContactMapper;
    private final UserContext userContext;

    @Override
    @Transactional(readOnly = true)
    public Optional<EmergencyContactDto> findByEmail(String email) {
        return emergencyContactRepository.findBySchoolIdAndEmail(userContext.getCurrentExternalId().get(), email)
                .map(EmergencyContactDto::fromEntity);
    }

    @Override
    @Transactional
    public EmergencyContactDto registerEmergencyContact(UUID studentId, EmergencyContactRequest request) {

        Student student = findActiveStudentById(studentId);

        EmergencyContact contact = resolveContact(student.getSchoolId(), request);

        boolean alreadyLinked = studentEmergencyContactRepository
                .existsByStudent_IdAndEmergencyContact_IdAndActiveTrue(student.getId(), contact.getId());

        if (alreadyLinked) {
            throw new BadRequestException(
                    "This contact is already linked to this student: emergencyContactId=" + contact.getId());
        }

        StudentEmergencyContact studentEmergencyContact = StudentEmergencyContact.builder()
                .student(student)
                .emergencyContact(contact)
                .relationship(request.getRelationship())
                .isPrimary(Boolean.TRUE.equals(request.getIsPrimary()))
                .active(true)
                .build();

        studentEmergencyContactRepository.save(studentEmergencyContact);

        return EmergencyContactDto.fromStudentEmergencyContact(studentEmergencyContact);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmergencyContactDto> getEmergencyContactsForStudent(UUID studentId) {
        return studentEmergencyContactRepository.findByStudent_IdAndActiveTrue(studentId).stream()
                .map(EmergencyContactDto::fromStudentEmergencyContact)
                .toList();
    }

    @Override
    @Transactional
    public void removeEmergencyContactFromStudent(UUID studentId, UUID emergencyContactId) {
        StudentEmergencyContact studentEmergencyContact = studentEmergencyContactRepository
                .findByStudent_IdAndEmergencyContact_Id(studentId, emergencyContactId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No emergency contact link found for studentId=" + studentId + ", emergencyContactId=" + emergencyContactId));
        studentEmergencyContact.setActive(false);
        studentEmergencyContactRepository.save(studentEmergencyContact);
    }

    @Override
    @Transactional
    public EmergencyContactDto updateEmergencyContact(UUID emergencyContactId, EmergencyContactRequest request) {
        EmergencyContact contact = emergencyContactRepository.findById(emergencyContactId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency contact not found with id: " + emergencyContactId));
        emergencyContactMapper.updateEntity(contact, request);
        EmergencyContact saved = emergencyContactRepository.save(contact);
        return EmergencyContactDto.fromEntity(saved);
    }

    private EmergencyContact resolveContact(UUID schoolId, EmergencyContactRequest request) {
        return emergencyContactRepository
                .findBySchoolIdAndEmail(schoolId, request.getEmail())
                .orElseGet(() -> emergencyContactRepository.save(emergencyContactMapper.toEntity(request)));
    }


    private Student findActiveStudentById(UUID id) {
        return studentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }
}
