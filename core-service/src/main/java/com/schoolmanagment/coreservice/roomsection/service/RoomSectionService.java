package com.schoolmanagment.coreservice.roomsection.service;

import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionDto;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionFilterRequest;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionRequest;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface RoomSectionService {

    Page<RoomSectionDto> getAllRoomSections(int page, int size);

    Page<RoomSectionDto> filterRoomSections(RoomSectionFilterRequest request);

    RoomSectionDto getRoomSectionById(UUID id);

    RoomSectionDto createRoomSection(RoomSectionRequest request);

    RoomSectionDto updateRoomSection(UUID id, RoomSectionRequest request);

    void deleteRoomSection(UUID id);
}
