package com.schoolmanagment.coreservice.classroom.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.classroom.dto.ClassRoomDto;
import com.schoolmanagment.coreservice.classroom.dto.ClassRoomFilterRequest;
import com.schoolmanagment.coreservice.classroom.dto.ClassRoomRequest;
import com.schoolmanagment.coreservice.classroom.service.ClassRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/classrooms", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ClassRoomController {

    private final ClassRoomService classRoomService;

    @GetMapping
    @RequiresPermission(resource = "CLASSROOMS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<ClassRoomDto>>> getAllClassRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ClassRoomDto> classRooms = classRoomService.getAllClassRooms(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(classRooms, "Classrooms retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "CLASSROOMS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<ClassRoomDto>>> filterClassRooms(
            @Valid @RequestBody ClassRoomFilterRequest request
    ) {
        Page<ClassRoomDto> classRooms = classRoomService.filterClassRooms(request);
        return ResponseEntity.ok(
                ApiResponse.success(classRooms, "Classrooms retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "CLASSROOMS", scope = "READ")
    public ResponseEntity<ApiResponse<ClassRoomDto>> getClassRoomById(@PathVariable UUID id) {
        ClassRoomDto classRoom = classRoomService.getClassRoomById(id);
        return ResponseEntity.ok(
                ApiResponse.success(classRoom, "Classroom retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "CLASSROOMS", scope = "CREATE")
    public ResponseEntity<ApiResponse<ClassRoomDto>> createClassRoom(
            @Valid @RequestBody ClassRoomRequest request
    ) {
        ClassRoomDto classRoom = classRoomService.createClassRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(classRoom, "Classroom created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "CLASSROOMS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<ClassRoomDto>> updateClassRoom(
            @PathVariable UUID id,
            @Valid @RequestBody ClassRoomRequest request
    ) {
        ClassRoomDto classRoom = classRoomService.updateClassRoom(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(classRoom, "Classroom updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "CLASSROOMS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteClassRoom(@PathVariable UUID id) {
        classRoomService.deleteClassRoom(id);
        return ResponseEntity.ok(
                ApiResponse.success("Classroom deleted successfully")
        );
    }
}
