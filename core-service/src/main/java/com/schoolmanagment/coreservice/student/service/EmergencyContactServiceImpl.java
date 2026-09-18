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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmergencyContactServiceImpl implements EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;
    private final EmergencyContactMapper emergencyContactMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<EmergencyContactDto> findByEmail(String email) {
        String normalizedEmail = requireNormalizedEmail(email);
        return emergencyContactRepository
                .findBySchoolIdAndEmailAndActiveTrue(currentSchoolId(), normalizedEmail)
                .map(emergencyContactMapper::toDto);
    }

    @Override
    @Transactional
    public void syncStudentLinks(Student student, List<EmergencyContactRequest> requests) {
        UUID schoolId = resolveSchoolId(student);
        validateRequestLinks(requests);

        Set<UUID> requestedContactIds = new HashSet<>();
        boolean primaryAssigned = false;
        for (EmergencyContactRequest request : requests) {
            EmergencyContact contact = resolveContact(request, schoolId);
            requestedContactIds.add(contact.getId());

            boolean isPrimary = Boolean.TRUE.equals(request.getIsPrimary()) && !primaryAssigned;
            if (isPrimary) {
                primaryAssigned = true;
            }
            upsertLink(student, contact, request, isPrimary);
        }

        deactivateUnrequestedLinks(student, requestedContactIds);
        if (!primaryAssigned) {
            assignFirstActiveAsPrimary(student);
        }
    }

    private EmergencyContact resolveContact(EmergencyContactRequest request, UUID schoolId) {
        if (request.getEmergencyContactId() != null) {
            EmergencyContact existing = findContactById(request.getEmergencyContactId(), schoolId);
            validateContactActive(existing);
            return existing;
        }

        String email = requireNormalizedEmail(request.getEmail());
        return emergencyContactRepository.findBySchoolIdAndEmail(schoolId, email)
                .map(contact -> {
                    validateContactActive(contact);
                    return contact;
                })
                .orElseGet(() -> createContact(request, schoolId, email));
    }

    private EmergencyContact createContact(EmergencyContactRequest request, UUID schoolId, String email) {
        validateNewContact(request);
        EmergencyContact contact = emergencyContactMapper.toEntity(request);
        contact.setEmail(email);
        contact.setSchoolId(schoolId);
        return emergencyContactRepository.save(contact);
    }

    private void upsertLink(
            Student student,
            EmergencyContact contact,
            EmergencyContactRequest request,
            boolean isPrimary
    ) {
        StudentEmergencyContact existing = findLink(student, contact.getId());
        if (existing != null) {
            existing.setRelationship(request.getRelationship());
            existing.setIsPrimary(isPrimary);
            existing.setActive(true);
            return;
        }
        student.linkEmergencyContact(contact, request.getRelationship(), isPrimary);
    }

    private void deactivateUnrequestedLinks(Student student, Set<UUID> requestedContactIds) {
        List<StudentEmergencyContact> links = student.getEmergencyContactLinks();
        if (links == null) {
            student.setEmergencyContactLinks(new ArrayList<>());
            return;
        }
        for (StudentEmergencyContact link : links) {
            UUID contactId = link.getEmergencyContact().getId();
            if (!requestedContactIds.contains(contactId)) {
                link.setActive(false);
                link.setIsPrimary(false);
            }
        }
    }

    private void assignFirstActiveAsPrimary(Student student) {
        List<StudentEmergencyContact> links = student.getEmergencyContactLinks();
        if (links == null) {
            return;
        }
        links.stream()
                .filter(link -> Boolean.TRUE.equals(link.getActive()))
                .findFirst()
                .ifPresent(link -> link.setIsPrimary(true));
    }

    private StudentEmergencyContact findLink(Student student, UUID contactId) {
        List<StudentEmergencyContact> links = student.getEmergencyContactLinks();
        if (links == null) {
            return null;
        }
        return links.stream()
                .filter(link -> link.getEmergencyContact() != null
                        && contactId.equals(link.getEmergencyContact().getId()))
                .findFirst()
                .orElse(null);
    }

    private EmergencyContact findContactById(UUID id, UUID schoolId) {
        return emergencyContactRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency contact not found with id: " + id));
    }

    private void validateRequestLinks(List<EmergencyContactRequest> requests) {
        Set<String> emails = new HashSet<>();
        Set<UUID> ids = new HashSet<>();
        int primaryCount = 0;
        for (EmergencyContactRequest request : requests) {
            String email = requireNormalizedEmail(request.getEmail());
            if (!emails.add(email)) {
                throw new BadRequestException("Duplicate emergency contact email '" + email + "'");
            }
            if (request.getEmergencyContactId() != null && !ids.add(request.getEmergencyContactId())) {
                throw new BadRequestException("Duplicate emergency contact assignment");
            }
            if (Boolean.TRUE.equals(request.getIsPrimary())) {
                primaryCount++;
            }
        }
        if (primaryCount > 1) {
            throw new BadRequestException("A student can have only one primary emergency contact");
        }
    }

    private void validateNewContact(EmergencyContactRequest request) {
        requireText(request.getFirstName(), "First name is required");
        requireText(request.getLastName(), "Last name is required");
        if (request.getBirthDate() == null) {
            throw new BadRequestException("Birth date is required");
        }
        if (request.getGender() == null) {
            throw new BadRequestException("Gender is required");
        }
        requireText(request.getNationality(), "Nationality is required");
        requireText(request.getSubCity(), "Sub city is required");
        requireText(request.getMobileNumber(), "Mobile number is required");
    }

    private void validateContactActive(EmergencyContact contact) {
        if (!Boolean.TRUE.equals(contact.getActive())) {
            throw new BadRequestException("Emergency contact with email '" + contact.getEmail() + "' is inactive");
        }
    }

    private String requireNormalizedEmail(String email) {
        String normalized = normalizeEmail(email);
        if (normalized == null) {
            throw new BadRequestException("Email is required");
        }
        return normalized;
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(message);
        }
    }

    private UUID resolveSchoolId(Student student) {
        if (student.getSchoolId() != null) {
            return student.getSchoolId();
        }
        UUID schoolId = currentSchoolId();
        student.setSchoolId(schoolId);
        return schoolId;
    }

    private UUID currentSchoolId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No schoolId on the current authentication — cannot save a school-scoped entity without one."));
    }
}
