package com.schoolmanagment.coreservice.roomsection.service;

import com.schoolmanagment.commonapplication.exception.BadRequestException;
import com.schoolmanagment.commonapplication.exception.ResourceNotFoundException;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.classroom.entity.ClassRoom;
import com.schoolmanagment.coreservice.classroom.repository.ClassRoomRepository;
import com.schoolmanagment.coreservice.classsection.entity.ClassSection;
import com.schoolmanagment.coreservice.classsection.repository.ClassSectionRepository;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionDto;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionFilterRequest;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionRequest;
import com.schoolmanagment.coreservice.roomsection.entity.RoomSection;
import com.schoolmanagment.coreservice.roomsection.mapper.RoomSectionMapper;
import com.schoolmanagment.coreservice.roomsection.repository.RoomSectionRepository;
import com.schoolmanagment.coreservice.roomsection.specification.RoomSectionSpecification;
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
public class RoomSectionServiceImpl implements RoomSectionService {

    private final RoomSectionRepository roomSectionRepository;
    private final RoomSectionMapper roomSectionMapper;
    private final ClassSectionRepository classSectionRepository;
    private final ClassRoomRepository classRoomRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<RoomSectionDto> getAllRoomSections(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roomSectionRepository.findByActiveTrue(pageable)
                .map(roomSectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomSectionDto> filterRoomSections(RoomSectionFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return roomSectionRepository.findAll(new RoomSectionSpecification(request), pageable)
                .map(roomSectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomSectionDto getRoomSectionById(UUID id) {
        return roomSectionMapper.toDto(findActiveRoomSectionById(id));
    }

    @Override
    @Transactional
    public RoomSectionDto createRoomSection(RoomSectionRequest request) {
        UUID schoolId = currentSchoolId();
        ClassSection classSection = resolveActiveClassSection(request.getClassSectionId());
        ClassRoom room = resolveActiveClassRoom(request.getRoomId());
        validateClassSectionBelongsToSchool(classSection, schoolId);
        validateClassRoomBelongsToSchool(room, schoolId);
        validateAssignmentNotTaken(schoolId, classSection.getId(), room.getId(), null);
        return roomSectionMapper.toDto(
                roomSectionRepository.save(roomSectionMapper.toEntity(request, classSection, room)));
    }

    @Override
    @Transactional
    public RoomSectionDto updateRoomSection(UUID id, RoomSectionRequest request) {
        RoomSection roomSection = findActiveRoomSectionById(id);
        ClassSection classSection = resolveActiveClassSection(request.getClassSectionId());
        ClassRoom room = resolveActiveClassRoom(request.getRoomId());
        validateClassSectionBelongsToSchool(classSection, roomSection.getSchoolId());
        validateClassRoomBelongsToSchool(room, roomSection.getSchoolId());
        validateAssignmentNotTaken(roomSection.getSchoolId(), classSection.getId(), room.getId(), id);
        roomSectionMapper.updateEntity(roomSection, classSection, room);
        return roomSectionMapper.toDto(roomSectionRepository.save(roomSection));
    }

    @Override
    @Transactional
    public void deleteRoomSection(UUID id) {
        RoomSection roomSection = findActiveRoomSectionById(id);
        roomSection.setActive(false);
        roomSectionRepository.save(roomSection);
    }

    private RoomSection findActiveRoomSectionById(UUID id) {
        return roomSectionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room section not found with id: " + id));
    }

    private ClassSection resolveActiveClassSection(UUID classSectionId) {
        return classSectionRepository.findByIdAndActiveTrue(classSectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Class section not found with id: " + classSectionId));
    }

    private ClassRoom resolveActiveClassRoom(UUID roomId) {
        return classRoomRepository.findByIdAndActiveTrue(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + roomId));
    }

    private void validateClassSectionBelongsToSchool(ClassSection classSection, UUID schoolId) {
        if (!schoolId.equals(classSection.getSchoolId())) {
            throw new BadRequestException("Class section does not belong to this school");
        }
    }

    private void validateClassRoomBelongsToSchool(ClassRoom room, UUID schoolId) {
        if (!schoolId.equals(room.getSchoolId())) {
            throw new BadRequestException("Classroom does not belong to this school");
        }
    }

    private void validateAssignmentNotTaken(UUID schoolId, UUID classSectionId, UUID roomId, UUID excludeId) {
        boolean taken = excludeId == null
                ? roomSectionRepository.existsBySchoolIdAndClassSection_IdAndRoom_Id(schoolId, classSectionId, roomId)
                : roomSectionRepository.existsBySchoolIdAndClassSection_IdAndRoom_IdAndIdNot(
                        schoolId, classSectionId, roomId, excludeId);
        if (taken) {
            throw new BadRequestException("Room is already assigned to this class section in this school");
        }
    }

    private UUID currentSchoolId() {
        return Optional.ofNullable(UserContext.current())
                .flatMap(UserContext::getCurrentExternalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No schoolId on the current authentication — cannot save a school-scoped entity without one."));
    }
}
