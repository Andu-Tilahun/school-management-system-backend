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
import java.util.Objects;
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

        StudentEmergencyContact link = StudentEmergencyContact.builder()
                .student(student)
                .emergencyContact(contact)
                .relationship(request.getRelationship())
                .isPrimary(Boolean.TRUE.equals(request.getIsPrimary()))
                .active(true)
                .build();
        studentEmergencyContactRepository.save(link);

        return EmergencyContactDto.fromLink(link);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmergencyContactDto> getEmergencyContactsForStudent(UUID studentId) {
        return studentEmergencyContactRepository.findByStudent_IdAndActiveTrue(studentId).stream()
                .map(EmergencyContactDto::fromLink)
                .toList();
    }

    @Override
    @Transactional
    public void removeEmergencyContactFromStudent(UUID studentId, UUID emergencyContactId) {
        StudentEmergencyContact link = studentEmergencyContactRepository
                .findByStudent_IdAndEmergencyContact_Id(studentId, emergencyContactId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No emergency contact link found for studentId=" + studentId + ", emergencyContactId=" + emergencyContactId));
        link.setActive(false);
        studentEmergencyContactRepository.save(link);
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
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return emergencyContactRepository.save(emergencyContactMapper.toEntity(request));
        }

        Optional<EmergencyContact> existing = emergencyContactRepository.findBySchoolIdAndEmail(schoolId, request.getEmail());
        if (existing.isPresent()) {
            EmergencyContact contact = existing.get();
            warnIfSubmittedDataDiffers(contact, request);
            return contact;
        }

        return emergencyContactRepository.save(emergencyContactMapper.toEntity(request));
    }

    private void warnIfSubmittedDataDiffers(EmergencyContact existing, EmergencyContactRequest request) {
        boolean nameDiffers = !Objects.equals(existing.getFirstName(), request.getFirstName())
                || !Objects.equals(existing.getLastName(), request.getLastName());
        boolean mobileDiffers = !Objects.equals(existing.getMobileNumber(), request.getMobileNumber());

        if (nameDiffers || mobileDiffers) {
            log.warn("Emergency contact matched by email ({}) but submitted data differs from what's on file. "
                            + "Existing record was reused unchanged.",
                    existing.getEmail());
        }
    }

    private Student findActiveStudentById(UUID id) {
        return studentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }
}
