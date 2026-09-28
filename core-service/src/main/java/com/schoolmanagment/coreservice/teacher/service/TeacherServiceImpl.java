package com.schoolmanagment.coreservice.teacher.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import com.schoolmanagment.coreservice.student.repository.EmergencyContactRepository;
import com.schoolmanagment.coreservice.student.repository.EnrollmentRepository;
import com.schoolmanagment.coreservice.student.repository.StudentEmergencyContactRepository;
import com.schoolmanagment.coreservice.student.repository.StudentRepository;
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
import com.schoolmanagment.coreservice.timetable.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherSubjectAssignmentRepository assignmentRepository;
    private final SubjectService subjectService;
    private final TeacherMapper teacherMapper;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TimetableRepository timetableRepository;
    private final StudentEmergencyContactRepository studentEmergencyContactRepository;
    private final EmergencyContactRepository emergencyContactRepository;

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
        validateMobileNumberNotTaken(request.getMobileNumber(), null);
        List<Subject> subjects = resolveSubjects(request.getSubjectIds());
        Teacher saved = teacherRepository.save(teacherMapper.toEntity(request));
        assignSubjectsToTeacher(saved, subjects);
        return toDto(saved);
    }

    @Override
    @Transactional
    public TeacherDto updateTeacher(UUID id, TeacherRequest request) {
        Teacher teacher = findActiveTeacherById(id);
        validateMobileNumberNotTaken(request.getMobileNumber(), id);
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
        Teacher teacher = currentLoggedInTeacher();
        return teacherMapper.toAssignmentDtos(
                assignmentRepository.findActiveByTeacherIdWithSubject(teacher.getId(), SubjectStatus.ACTIVE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentDto> getSubjectsForCurrentStudent() {
        UUID studentId = currentStudentId();
        studentRepository.findByIdAndActiveTrue(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return subjectsForStudent(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAssignmentDto> getSubjectsByStudentId(UUID studentId) {
        UUID emergencyContactId = currentEmergencyContactId();
        if (!studentEmergencyContactRepository
                .existsByStudent_IdAndEmergencyContact_IdAndActiveTrue(studentId, emergencyContactId)) {
            throw new BadRequestException("Student is not linked to the logged-in emergency contact");
        }
        studentRepository.findByIdAndActiveTrue(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
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
        return enrollmentRepository
                .findActiveClassSectionIdByStudentId(studentId, EnrollmentStatus.ACTIVE)
                .map(classSectionId -> timetableRepository
                        .findActiveSubjectsByClassSectionId(classSectionId, SubjectStatus.ACTIVE)
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

    private UUID currentStudentId() {
        return UserContext.current().getCurrentExternalId()
                .orElseThrow(() -> new BadRequestException("Logged-in student has no external id"));
    }

    private UUID currentEmergencyContactId() {
        UUID emergencyContactId = UserContext.current().getCurrentExternalId()
                .orElseThrow(() -> new BadRequestException("Logged-in emergency contact has no external id"));
        EmergencyContact contact = emergencyContactRepository.findById(emergencyContactId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Emergency contact not found with id: " + emergencyContactId));
        if (!Boolean.TRUE.equals(contact.getActive())) {
            throw new BadRequestException("Emergency contact is not active");
        }
        return emergencyContactId;
    }

    private Teacher currentLoggedInTeacher() {
        UUID teacherId = UserContext.current().getCurrentExternalId()
                .orElseThrow(() -> new BadRequestException("Logged-in teacher has no external id"));
        return findActiveTeacherById(teacherId);
    }

    private Teacher findActiveTeacherById(UUID id) {
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

    private void validateMobileNumberNotTaken(String mobileNumber, UUID excludeId) {
        teacherRepository.findByMobileNumber(mobileNumber).ifPresent(existing -> {
            if (!existing.getId().equals(excludeId)) {
                throw new BadRequestException("Teacher with mobile number '" + mobileNumber + "' already exists in this school");
            }
        });
    }
}
