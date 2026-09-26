package com.schoolmanagment.coreservice.classsection.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionHomeroomDto;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionHomeroomRequest;
import com.schoolmanagment.coreservice.classsection.service.ClassSectionHomeroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/class-section-homerooms", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ClassSectionHomeroomController {

    private final ClassSectionHomeroomService classSectionHomeroomService;

    @PostMapping
    @RequiresPermission(resource = "CLASS_SECTIONS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<ClassSectionHomeroomDto>> reassign(
            @Valid @RequestBody ClassSectionHomeroomRequest request) {
        ClassSectionHomeroomDto homeroom = classSectionHomeroomService.reassign(
                request.getClassSectionId(), request.getTeacherId());
        return ResponseEntity.ok(
                ApiResponse.success(homeroom, "Homeroom teacher assigned successfully")
        );
    }

    @GetMapping("/class-section/{classSectionId}/current")
    @RequiresPermission(resource = "CLASS_SECTIONS", scope = "READ")
    public ResponseEntity<ApiResponse<ClassSectionHomeroomDto>> getCurrent(@PathVariable UUID classSectionId) {
        ClassSectionHomeroomDto homeroom = classSectionHomeroomService.getCurrent(classSectionId);
        return ResponseEntity.ok(
                ApiResponse.success(homeroom, "Current homeroom teacher retrieved successfully")
        );
    }

    @GetMapping("/class-section/{classSectionId}/history")
    @RequiresPermission(resource = "CLASS_SECTIONS", scope = "READ")
    public ResponseEntity<ApiResponse<List<ClassSectionHomeroomDto>>> getHistory(@PathVariable UUID classSectionId) {
        List<ClassSectionHomeroomDto> history = classSectionHomeroomService.getHistory(classSectionId);
        return ResponseEntity.ok(
                ApiResponse.success(history, "Homeroom history retrieved successfully")
        );
    }

    @GetMapping("/teacher/{teacherId}")
    @RequiresPermission(resource = "CLASS_SECTIONS", scope = "READ")
    public ResponseEntity<ApiResponse<List<ClassSectionHomeroomDto>>> getByTeacher(@PathVariable UUID teacherId) {
        List<ClassSectionHomeroomDto> homerooms = classSectionHomeroomService.getByTeacher(teacherId);
        return ResponseEntity.ok(
                ApiResponse.success(homerooms, "Homeroom assignments retrieved successfully")
        );
    }
}
