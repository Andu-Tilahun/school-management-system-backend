package com.schoolmanagment.coreservice.teacher.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.subject.service.SubjectService;
import com.schoolmanagment.coreservice.teacher.dto.TeacherDto;
import com.schoolmanagment.coreservice.teacher.dto.TeacherFilterRequest;
import com.schoolmanagment.coreservice.teacher.dto.TeacherRequest;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.teacher.entity.TeacherSubjectAssignment;
import com.schoolmanagment.coreservice.teacher.mapper.TeacherMapper;
import com.schoolmanagment.coreservice.teacher.repository.TeacherRepository;
import com.schoolmanagment.coreservice.teacher.repository.TeacherSubjectAssignmentRepository;
import com.schoolmanagment.coreservice.teacher.specification.TeacherSpecification;
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
