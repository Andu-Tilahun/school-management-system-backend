package com.schoolmanagment.coreservice.timetable.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionDto;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.classsection.mapper.ClassSectionMapper;
import com.schoolmanagment.coreservice.classsection.service.ClassSectionService;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import com.schoolmanagment.coreservice.teacher.entity.TeacherSubjectAssignment;
import com.schoolmanagment.coreservice.teacher.service.TeacherService;
import com.schoolmanagment.coreservice.timetable.dto.TimetableDto;
import com.schoolmanagment.coreservice.timetable.dto.TimetableFilterRequest;
import com.schoolmanagment.coreservice.timetable.dto.TimetableRequest;
import com.schoolmanagment.coreservice.timetable.entity.Timetable;
import com.schoolmanagment.coreservice.timetable.enums.Day;
import com.schoolmanagment.coreservice.timetable.enums.Period;
import com.schoolmanagment.coreservice.timetable.mapper.TimetableMapper;
import com.schoolmanagment.coreservice.timetable.repository.TimetableRepository;
import com.schoolmanagment.coreservice.timetable.specification.TimetableSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {

    private final TimetableRepository timetableRepository;
    private final TimetableMapper timetableMapper;
    private final ClassSectionMapper classSectionMapper;
    private final ClassSectionService classSectionService;
    private final TeacherService teacherService;

    @Override
    @Transactional(readOnly = true)
    public Page<TimetableDto> getAllTimetables(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return timetableRepository.findByActiveTrue(pageable)
                .map(timetableMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimetableDto> filterTimetables(TimetableFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return timetableRepository.findAll(new TimetableSpecification(request), pageable)
                .map(timetableMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TimetableDto getTimetableById(UUID id) {
        return timetableMapper.toDto(findActiveTimetableById(id));
    }

    @Override
    @Transactional
    public TimetableDto createTimetable(TimetableRequest request) {
        ClassSection classSection = classSectionService.findActiveClassSectionById(request.getClassSectionId());
        TeacherSubjectAssignment assignment = teacherService.findActiveTeacherSubjectAssignmentById(
                request.getTeacherSubjectAssignmentId());

        validateSubjectBelongsToSectionGrade(assignment.getSubject(), classSection);
        validateSlotAvailable(
                classSection.getId(), assignment.getTeacher().getId(), request.getDay(), request.getPeriod(), null);

        try {
            Timetable saved = timetableRepository.save(
                    timetableMapper.toEntity(request, classSection, assignment));
            return timetableMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(
                    "This teacher or class section was just booked for this day and period");
        }
    }

    @Override
    @Transactional
    public TimetableDto updateTimetable(UUID id, TimetableRequest request) {
        Timetable timetable = findActiveTimetableById(id);
        ClassSection classSection = classSectionService.findActiveClassSectionById(request.getClassSectionId());
        TeacherSubjectAssignment assignment = teacherService.findActiveTeacherSubjectAssignmentById(
                request.getTeacherSubjectAssignmentId());

        validateSubjectBelongsToSectionGrade(assignment.getSubject(), classSection);
        validateSlotAvailable(
                classSection.getId(), assignment.getTeacher().getId(), request.getDay(), request.getPeriod(), id);

        timetableMapper.updateEntity(timetable, request, classSection, assignment);

        try {
            return timetableMapper.toDto(timetableRepository.save(timetable));
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(
                    "This teacher or class section was just booked for this day and period");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionDto> getSectionsByTeacher(UUID teacherId) {
        teacherService.getTeacherById(teacherId);
        return timetableRepository.findActiveClassSectionsByTeacherId(teacherId).stream()
                .map(classSectionMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionDto> getSectionsForCurrentTeacher() {
        UUID teacherId = UserContext.current().getCurrentExternalId()
                .orElseThrow(() -> new BadRequestException("Logged-in teacher has no external id"));
        return getSectionsByTeacher(teacherId);
    }

    @Override
    @Transactional
    public void deleteTimetable(UUID id) {
        Timetable timetable = findActiveTimetableById(id);
        timetable.setActive(false);
        timetableRepository.save(timetable);
    }

    private Timetable findActiveTimetableById(UUID id) {
        return timetableRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable not found with id: " + id));
    }

    private void validateSubjectBelongsToSectionGrade(Subject subject, ClassSection classSection) {
        UUID subjectGradeId = subject.getGrade() != null ? subject.getGrade().getId() : null;
        UUID sectionGradeId = classSection.getGrade() != null ? classSection.getGrade().getId() : null;
        if (subjectGradeId == null || !subjectGradeId.equals(sectionGradeId)) {
            throw new BadRequestException("Subject does not belong to this class section's grade");
        }
    }

    private void validateSlotAvailable(
            UUID classSectionId, UUID teacherId, Day day, Period period, UUID excludeId) {

        if (timetableRepository.existsConflictingClassSectionSlot(classSectionId, day, period, excludeId)) {
            throw new BadRequestException(
                    "This class section already has a subject scheduled at this day and period");
        }

        if (timetableRepository.existsConflictingTeacherSlot(teacherId, day, period, excludeId)) {
            throw new BadRequestException("This teacher is already scheduled at this day and period");
        }
    }
}
