package com.schoolmanagment.coreservice.classsection.service;

import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionHomeroomDto;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.classsection.entity.ClassSectionHomeroom;
import com.schoolmanagment.coreservice.classsection.mapper.ClassSectionHomeroomMapper;
import com.schoolmanagment.coreservice.classsection.repository.ClassSectionHomeroomRepository;
import com.schoolmanagment.coreservice.classsection.repository.ClassSectionRepository;
import com.schoolmanagment.coreservice.teacher.entity.Teacher;
import com.schoolmanagment.coreservice.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassSectionHomeroomServiceImpl implements ClassSectionHomeroomService {

    private final ClassSectionHomeroomRepository classSectionHomeroomRepository;
    private final ClassSectionRepository classSectionRepository;
    private final TeacherRepository teacherRepository;
    private final ClassSectionHomeroomMapper classSectionHomeroomMapper;

    @Override
    @Transactional
    public ClassSectionHomeroomDto reassign(UUID classSectionId, UUID newTeacherId) {
        ClassSection classSection = classSectionRepository.findByIdAndActiveTrue(classSectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Class section not found with id: " + classSectionId));

        Teacher newTeacher = teacherRepository.findByIdAndActiveTrue(newTeacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + newTeacherId));

        Optional<ClassSectionHomeroom> current =
                classSectionHomeroomRepository.findByClassSectionIdAndActiveTrue(classSectionId);

        if (current.isPresent() && current.get().getTeacher().getId().equals(newTeacherId)) {
            return classSectionHomeroomMapper.toDto(current.get());
        }

        current.ifPresent(existing -> {
            existing.setActive(false);
            classSectionHomeroomRepository.save(existing);
        });

        ClassSectionHomeroom newAssignment = ClassSectionHomeroom.builder()
                .classSection(classSection)
                .teacher(newTeacher)
                .active(true)
                .build();

        return classSectionHomeroomMapper.toDto(
                classSectionHomeroomRepository.save(newAssignment));
    }

    @Override
    @Transactional(readOnly = true)
    public ClassSectionHomeroomDto getCurrent(UUID classSectionId) {
        ClassSectionHomeroom current = classSectionHomeroomRepository
                .findByClassSectionIdAndActiveTrue(classSectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active homeroom teacher assigned for this class section"));
        return classSectionHomeroomMapper.toDto(current);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionHomeroomDto> getHistory(UUID classSectionId) {
        return classSectionHomeroomMapper.toDtoList(
                classSectionHomeroomRepository.findByClassSectionIdOrderByCreatedAtDesc(classSectionId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionHomeroomDto> getByTeacher(UUID teacherId) {
        return classSectionHomeroomMapper.toDtoList(
                classSectionHomeroomRepository.findByTeacherIdAndActiveTrue(teacherId));
    }
}
