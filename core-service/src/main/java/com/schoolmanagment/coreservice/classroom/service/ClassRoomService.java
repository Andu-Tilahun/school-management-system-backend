package com.schoolmanagment.coreservice.classroom.service;

import com.schoolmanagment.coreservice.classroom.dto.ClassRoomDto;
import com.schoolmanagment.coreservice.classroom.dto.ClassRoomFilterRequest;
import com.schoolmanagment.coreservice.classroom.dto.ClassRoomRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ClassRoomService {

    Page<ClassRoomDto> getAllClassRooms(int page, int size);

    Page<ClassRoomDto> filterClassRooms(ClassRoomFilterRequest request);

    ClassRoomDto getClassRoomById(UUID id);

    ClassRoomDto createClassRoom(ClassRoomRequest request);

    ClassRoomDto updateClassRoom(UUID id, ClassRoomRequest request);

    void deleteClassRoom(UUID id);
}
