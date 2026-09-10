package com.schoolmanagment.coreservice.classroom.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.classroom.dto.ClassRoomDto;
import com.schoolmanagment.coreservice.classroom.dto.ClassRoomFilterRequest;
import com.schoolmanagment.coreservice.classroom.dto.ClassRoomRequest;
import com.schoolmanagment.coreservice.classroom.entity.ClassRoom;
import com.schoolmanagment.coreservice.classroom.mapper.ClassRoomMapper;
import com.schoolmanagment.coreservice.classroom.repository.ClassRoomRepository;
import com.schoolmanagment.coreservice.classroom.specification.ClassRoomSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassRoomServiceImpl implements ClassRoomService {

    private final ClassRoomRepository classRoomRepository;
    private final ClassRoomMapper classRoomMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ClassRoomDto> getAllClassRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return classRoomRepository.findByActiveTrue(pageable)
                .map(classRoomMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassRoomDto> filterClassRooms(ClassRoomFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return classRoomRepository.findAll(new ClassRoomSpecification(request), pageable)
                .map(classRoomMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassRoomDto getClassRoomById(UUID id) {
        return classRoomMapper.toDto(findActiveClassRoomById(id));
    }

    @Override
    @Transactional
    public ClassRoomDto createClassRoom(ClassRoomRequest request) {
        validateRoomNumberNotTaken(currentSchoolId(), request.getRoomNumber(), null);
        return classRoomMapper.toDto(classRoomRepository.save(classRoomMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public ClassRoomDto updateClassRoom(UUID id, ClassRoomRequest request) {
        ClassRoom classRoom = findActiveClassRoomById(id);
        validateRoomNumberNotTaken(classRoom.getSchoolId(), request.getRoomNumber(), id);
        classRoomMapper.updateEntity(classRoom, request);
        return classRoomMapper.toDto(classRoomRepository.save(classRoom));
    }

    @Override
    @Transactional
    public void deleteClassRoom(UUID id) {
        ClassRoom classRoom = findActiveClassRoomById(id);
        classRoom.setActive(false);
        classRoomRepository.save(classRoom);
    }

    private ClassRoom findActiveClassRoomById(UUID id) {
        return classRoomRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + id));
    }

    private void validateRoomNumberNotTaken(UUID schoolId, String roomNumber, UUID excludeId) {
        boolean taken = excludeId == null
                ? classRoomRepository.existsBySchoolIdAndRoomNumberIgnoreCase(schoolId, roomNumber)
                : classRoomRepository.existsBySchoolIdAndRoomNumberIgnoreCaseAndIdNot(schoolId, roomNumber, excludeId);
        if (taken) {
            throw new BadRequestException("Classroom with room number '" + roomNumber + "' already exists in this school");
        }
    }

    private UUID currentSchoolId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No schoolId on the current authentication — cannot save a school-scoped entity without one."));
    }
}
