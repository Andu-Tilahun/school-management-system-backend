package com.schoolmanagment.coreservice.roomsection.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionDto;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionFilterRequest;
import com.schoolmanagment.coreservice.roomsection.dto.RoomSectionRequest;
import com.schoolmanagment.coreservice.roomsection.service.RoomSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/room-sections", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RoomSectionController {

    private final RoomSectionService roomSectionService;

    @GetMapping
    @RequiresPermission(resource = "ROOM_SECTIONS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<RoomSectionDto>>> getAllRoomSections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<RoomSectionDto> roomSections = roomSectionService.getAllRoomSections(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(roomSections, "Room sections retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "ROOM_SECTIONS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<RoomSectionDto>>> filterRoomSections(
            @Valid @RequestBody RoomSectionFilterRequest request
    ) {
        Page<RoomSectionDto> roomSections = roomSectionService.filterRoomSections(request);
        return ResponseEntity.ok(
                ApiResponse.success(roomSections, "Room sections retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "ROOM_SECTIONS", scope = "READ")
    public ResponseEntity<ApiResponse<RoomSectionDto>> getRoomSectionById(@PathVariable UUID id) {
        RoomSectionDto roomSection = roomSectionService.getRoomSectionById(id);
        return ResponseEntity.ok(
                ApiResponse.success(roomSection, "Room section retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "ROOM_SECTIONS", scope = "CREATE")
    public ResponseEntity<ApiResponse<RoomSectionDto>> createRoomSection(
            @Valid @RequestBody RoomSectionRequest request
    ) {
        RoomSectionDto roomSection = roomSectionService.createRoomSection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(roomSection, "Room section created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "ROOM_SECTIONS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<RoomSectionDto>> updateRoomSection(
            @PathVariable UUID id,
            @Valid @RequestBody RoomSectionRequest request
    ) {
        RoomSectionDto roomSection = roomSectionService.updateRoomSection(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(roomSection, "Room section updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "ROOM_SECTIONS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteRoomSection(@PathVariable UUID id) {
        roomSectionService.deleteRoomSection(id);
        return ResponseEntity.ok(
                ApiResponse.success("Room section deleted successfully")
        );
    }
}
