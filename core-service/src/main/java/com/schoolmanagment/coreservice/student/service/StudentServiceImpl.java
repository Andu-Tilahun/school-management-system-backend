package com.schoolmanagment.coreservice.student.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.student.dto.StudentFilterRequest;
import com.schoolmanagment.coreservice.student.dto.StudentRequest;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.mapper.StudentMapper;
import com.schoolmanagment.coreservice.student.repository.StudentRepository;
import com.schoolmanagment.coreservice.student.specification.StudentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public Page<StudentDto> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studentRepository.findByActiveTrue(pageable)
                .map(studentMapper::toDto);
    }

    @Override
    public Page<StudentDto> filterStudents(StudentFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return studentRepository.findAll(new StudentSpecification(request), pageable)
                .map(studentMapper::toDto);
    }

    @Override
    public StudentDto getStudentById(UUID id) {
        return studentMapper.toDto(findActiveStudentById(id));
    }

    @Override
    @Transactional
    public StudentDto createStudent(StudentRequest request) {
        validateMobileNumberNotTaken(request.getMobileNumber(), null);

        return studentMapper.toDto(studentRepository.save(studentMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public StudentDto updateStudent(UUID id, StudentRequest request) {
        Student student = findActiveStudentById(id);
        validateMobileNumberNotTaken(request.getMobileNumber(), id);

        studentMapper.updateEntity(student, request);

        return studentMapper.toDto(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void deleteStudent(UUID id) {
        Student student = findActiveStudentById(id);
        student.setActive(false);
        studentRepository.save(student);
    }

    private Student findActiveStudentById(UUID id) {
        return studentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    private void validateMobileNumberNotTaken(String mobileNumber, UUID excludeId) {
        studentRepository.findByMobileNumber(mobileNumber).ifPresent(existing -> {
            if (!existing.getId().equals(excludeId)) {
                throw new BadRequestException("Student with mobile number '" + mobileNumber + "' already exists");
            }
        });
    }
}
