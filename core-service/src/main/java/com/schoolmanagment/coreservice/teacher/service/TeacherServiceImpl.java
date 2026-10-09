package com.schoolmanagment.coreservice.teacher.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.PolicyNames;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.client.InternalUserRegisterRequest;
import com.schoolmanagment.coreservice.client.UserInternalClient;
import com.schoolmanagment.coreservice.student.service.EmergencyContactService;
import com.schoolmanagment.coreservice.student.service.EnrollmentService;
import com.schoolmanagment.coreservice.student.service.StudentService;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import com.schoolmanagment.coreservice.subject.service.SubjectService;
import com.schoolmanagment.coreservice.teacher.dto.TeacherDto;
import com.schoolmanagment.coreservice.teacher.dto.TeacherFilterRequest;
import com.schoolmanagment.coreservice.teacher.dto.TeacherRequest;
import com.schoolmanagment.coreservice.teacher.dto.TeacherSubjectAssignmentDto;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.teacher.entity.TeacherSubjectAssignment;
import com.schoolmanagment.coreservice.teacher.mapper.TeacherMapper;
import com.schoolmanagment.coreservice.teacher.repository.TeacherRepository;
import com.schoolmanagment.coreservice.teacher.repository.TeacherSubjectAssignmentRepository;
import com.schoolmanagment.coreservice.teacher.specification.TeacherSpecification;
import com.schoolmanagment.coreservice.timetable.service.TimetableService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    @Value("${teacher.default-password:ChangeMe123!}")
    private String defaultTeacherPassword;

    private final TeacherRepository teacherRepository;
    private final TeacherSubjectAssignmentRepository assignmentRepository;
    private final SubjectService subjectService;
    private final TeacherMapper teacherMapper;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    @Lazy
    private final TimetableService timetableService;
    private final EmergencyContactService emergencyContactService;
    private final UserInternalClient userInternalClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherDto> getAllTeachers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return teacherRepository.findByActiveTrue(pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherDto> filterTeachers(TeacherFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return teacherRepository.findAll(new TeacherSpecification(request), pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherDto getTeacherById(UUID id) {
        return toDto(findActiveTeacherById(id));
    }

    @Override
    @Transactional
    public TeacherDto createTeacher(TeacherRequest request) {
        validateMinimumAge(request.getBirthDate());
        validateMobileNumberNotTaken(request.getMobileNumber(), null);
        validateEmailNotTaken(request.getEmail(), null);
        List<Subject> subjects = resolveSubjects(request.getSubjectIds());
        Teacher saved = teacherRepository.save(teacherMapper.toEntity(request));
        assignSubjectsToTeacher(saved, subjects);
        return toDto(saved);
    }

    @Override
    @Transactional
    public TeacherDto createTeacherAccount(UUID id) {
        Teacher teacher = findActiveTeacherById(id);
        if (Boolean.TRUE.equals(teacher.getHasAccount())) {
            throw new BadRequestException("Teacher already has an account");
        }
        if (teacher.getSchoolId() == null) {
            throw new BadRequestException("Teacher is not assigned to a school");
        }
        String email = teacher.getEmail() == null ? "" : teacher.getEmail().trim().toLowerCase();
        if (email.isBlank()) {
            throw new BadRequestException("Teacher email is required to create an account");
        }
        if (defaultTeacherPassword == null || defaultTeacherPassword.isBlank()) {
            throw new BadRequestException("Teacher default password is not configured");
        }

        InternalUserRegisterRequest registerRequest = InternalUserRegisterRequest.builder()
                .id(teacher.getId())
                .username(email)
                .password(defaultTeacherPassword)
                .email(email)
                .firstName(teacher.getFirstName())
                .middleName(teacher.getMiddleName())
                .lastName(teacher.getLastName())
                .gender(teacher.getGender().name())
                .policyNames(List.of(PolicyNames.TEACHER_POLICY))
                .externalId(teacher.getSchoolId())
                .build();

        try {
            userInternalClient.register(registerRequest);
        } catch (FeignException ex) {
            throw new BadRequestException(userServiceMessage(ex));
        }

        teacher.setHasAccount(true);
        return toDto(teacherRepository.save(teacher));
    }

    @Override
    @Transactional
    public TeacherDto updateTeacher(UUID id, TeacherRequest request) {
        Teacher teacher = findActiveTeacherById(id);
        validateMinimumAge(request.getBirthDate());
        validateMobileNumberNotTaken(request.getMobileNumber(), id);
        validateEmailNotTaken(request.getEmail(), id);
        List<Subject> subjects = resolveSubjects(request.getSubjectIds());
        teacherMapper.updateEntity(teacher, request);
        Teacher saved = teacherRepository.save(teacher);
        assignSubjectsToTeacher(saved, subjects);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteTeacher(UUID id) {
        Teacher teacher = findActiveTeacherById(id);
        teacher.setActive(false);
        teacherRepository.save(teacher);
        List<TeacherSubjectAssignment> assignments = assignmentRepository.findByTeacherIdAndActiveTrue(id);
        assignments.forEach(assignment -> assignment.setActive(false));
        assignmentRepository.saveAll(assignments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentDto> getAssignedSubjects() {
        Teacher teacher = findActiveTeacherById(currentLoggedInUserId());
        return teacherMapper.toAssignmentDtos(
                assignmentRepository.findActiveByTeacherIdWithSubject(teacher.getId(), SubjectStatus.ACTIVE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherDto> getTeachersBySubject(UUID subjectId) {
        UUID schoolId = UserContext.current().getCurrentExternalId().orElse(null);
        return assignmentRepository.findActiveBySubjectIdWithTeacher(subjectId, SubjectStatus.ACTIVE).stream()
                .filter(assignment -> schoolId == null || schoolId.equals(assignment.getSchoolId()))
                .map(assignment -> teacherMapper.toDto(assignment.getTeacher(), List.of(assignment)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentDto> getSubjectsForCurrentStudent() {
        UUID studentId = currentLoggedInUserId();
        studentService.findActiveStudentById(studentId);
        return subjectsForStudent(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentDto> getSubjectsByStudentId(UUID studentId) {
        UUID emergencyContactId = currentLoggedInUserId();
        emergencyContactService.findActiveEmergencyContactById(emergencyContactId);
        if (!emergencyContactService.isStudentLinkedToEmergencyContact(studentId, emergencyContactId)) {
            throw new BadRequestException("Student is not linked to the logged-in emergency contact");
        }
        studentService.findActiveStudentById(studentId);
        return subjectsForStudent(studentId);
    }

    @Override
    public TeacherSubjectAssignment findActiveTeacherSubjectAssignmentById(UUID id) {
        TeacherSubjectAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher subject assignment not found with id: " + id));
        if (!Boolean.TRUE.equals(assignment.getActive())) {
            throw new BadRequestException("Teacher subject assignment is not active");
        }
        Teacher teacher = assignment.getTeacher();
        if (teacher == null || !Boolean.TRUE.equals(teacher.getActive())) {
            throw new BadRequestException("Teacher is not active");
        }
        Subject subject = assignment.getSubject();
        if (subject == null || subject.getStatus() != SubjectStatus.ACTIVE) {
            throw new BadRequestException("Subject is not active");
        }
        return assignment;
    }

    private List<TeacherSubjectAssignmentDto> subjectsForStudent(UUID studentId) {
        return enrollmentService.findActiveClassSectionIdByStudentId(studentId)
                .map(classSectionId -> timetableService.findActiveSubjectsByClassSectionId(classSectionId)
                        .stream()
                        .map(this::toSubjectDto)
                        .toList())
                .orElseGet(List::of);
    }

    private TeacherSubjectAssignmentDto toSubjectDto(Subject subject) {
        return TeacherSubjectAssignmentDto.builder()
                .id(subject.getId())
                .subjectId(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .active(subject.getStatus() == SubjectStatus.ACTIVE)
                .build();
    }

    private UUID currentLoggedInUserId() {
        return UserContext.current().getCurrentUserId();
    }

    @Override
    public Teacher findActiveTeacherById(UUID id) {
        return teacherRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }

    private TeacherDto toDto(Teacher teacher) {
        return teacherMapper.toDto(
                teacher,
                assignmentRepository.findByTeacherIdAndActiveTrue(teacher.getId())
        );
    }

    private List<Subject> resolveSubjects(List<UUID> subjectIds) {
        Set<UUID> uniqueIds = new HashSet<>(subjectIds);
        if (uniqueIds.size() != subjectIds.size()) {
            throw new BadRequestException("Duplicate subjects are not allowed");
        }

        return uniqueIds.stream()
                .map(subjectService::findActiveSubjectById)
                .toList();
    }

    private void assignSubjectsToTeacher(Teacher teacher, List<Subject> subjects) {
        List<TeacherSubjectAssignment> existing = assignmentRepository.findByTeacherId(teacher.getId());
        Map<UUID, TeacherSubjectAssignment> bySubjectId = new HashMap<>();
        for (TeacherSubjectAssignment assignment : existing) {
            bySubjectId.put(assignment.getSubject().getId(), assignment);
            assignment.setActive(false);
        }

        List<TeacherSubjectAssignment> toSave = new ArrayList<>(existing);
        for (Subject subject : subjects) {
            TeacherSubjectAssignment assignment = bySubjectId.get(subject.getId());
            if (assignment == null) {
                toSave.add(TeacherSubjectAssignment.builder()
                        .teacher(teacher)
                        .subject(subject)
                        .active(true)
                        .build());
            } else {
                assignment.setActive(true);
            }
        }
        assignmentRepository.saveAll(toSave);
    }

    private void validateMinimumAge(LocalDate birthDate) {
        if (birthDate != null && birthDate.isAfter(LocalDate.now().minusYears(21))) {
            throw new BadRequestException("Teacher must be at least 21 years old");
        }
    }

    private String userServiceMessage(FeignException ex) {
        String body = ex.contentUTF8();
        if (body != null && !body.isBlank()) {
            try {
                JsonNode message = objectMapper.readTree(body).path("message");
                if (message.isTextual() && !message.asText().isBlank()) {
                    return message.asText();
                }
            } catch (Exception ignored) {
                // Fall through to the generic message.
            }
        }
        return "Could not create the teacher account";
    }

    private void validateEmailNotTaken(String email, UUID excludeId) {
        if (email == null || email.isBlank()) {
            return;
        }
        teacherRepository.findByEmail(email.trim().toLowerCase()).ifPresent(existing -> {
            if (!existing.getId().equals(excludeId)) {
                throw new BadRequestException("Teacher with email '" + email.trim() + "' already exists");
            }
        });
    }

    private void validateMobileNumberNotTaken(String mobileNumber, UUID excludeId) {
        teacherRepository.findByMobileNumber(mobileNumber).ifPresent(existing -> {
            if (!existing.getId().equals(excludeId)) {
                throw new BadRequestException("Teacher with mobile number '" + mobileNumber + "' already exists in this school");
            }
        });
    }
}
