package com.schoolmanagment.coreservice.teacher.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.teacher.dto.TeacherDto;
import com.schoolmanagment.coreservice.teacher.dto.TeacherFilterRequest;
import com.schoolmanagment.coreservice.teacher.dto.TeacherRequest;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.teacher.mapper.TeacherMapper;
import com.schoolmanagment.coreservice.teacher.repository.TeacherRepository;
import com.schoolmanagment.coreservice.teacher.specification.TeacherSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherDto> getAllTeachers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return teacherRepository.findByActiveTrue(pageable)
                .map(teacherMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherDto> filterTeachers(TeacherFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return teacherRepository.findAll(new TeacherSpecification(request), pageable)
                .map(teacherMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherDto getTeacherById(UUID id) {
        return teacherMapper.toDto(findActiveTeacherById(id));
    }

    @Override
    @Transactional
    public TeacherDto createTeacher(TeacherRequest request) {
        validateMobileNumberNotTaken(request.getMobileNumber(), null);
        return teacherMapper.toDto(teacherRepository.save(teacherMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public TeacherDto updateTeacher(UUID id, TeacherRequest request) {
        Teacher teacher = findActiveTeacherById(id);
        validateMobileNumberNotTaken(request.getMobileNumber(), id);
        teacherMapper.updateEntity(teacher, request);
        return teacherMapper.toDto(teacherRepository.save(teacher));
    }

    @Override
    @Transactional
    public void deleteTeacher(UUID id) {
        Teacher teacher = findActiveTeacherById(id);
        teacher.setActive(false);
        teacherRepository.save(teacher);
    }

    private Teacher findActiveTeacherById(UUID id) {
        return teacherRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }

    private void validateMobileNumberNotTaken(String mobileNumber, UUID excludeId) {
        teacherRepository.findByMobileNumber(mobileNumber).ifPresent(existing -> {
            if (!existing.getId().equals(excludeId)) {
                throw new BadRequestException("Teacher with mobile number '" + mobileNumber + "' already exists in this school");
            }
        });
    }
}
