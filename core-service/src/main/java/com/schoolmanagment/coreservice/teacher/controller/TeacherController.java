package com.schoolmanagment.coreservice.teacher.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.teacher.dto.TeacherDto;
import com.schoolmanagment.coreservice.teacher.dto.TeacherFilterRequest;
import com.schoolmanagment.coreservice.teacher.dto.TeacherRequest;
import com.schoolmanagment.coreservice.teacher.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/teachers", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @RequiresPermission(resource = "TEACHERS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<TeacherDto>>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TeacherDto> teachers = teacherService.getAllTeachers(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(teachers, "Teachers retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "TEACHERS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<TeacherDto>>> filterTeachers(
            @Valid @RequestBody TeacherFilterRequest request
    ) {
        Page<TeacherDto> teachers = teacherService.filterTeachers(request);
        return ResponseEntity.ok(
                ApiResponse.success(teachers, "Teachers retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "TEACHERS", scope = "READ")
    public ResponseEntity<ApiResponse<TeacherDto>> getTeacherById(@PathVariable UUID id) {
        TeacherDto teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(
                ApiResponse.success(teacher, "Teacher retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "TEACHERS", scope = "CREATE")
    public ResponseEntity<ApiResponse<TeacherDto>> createTeacher(
            @Valid @RequestBody TeacherRequest request
    ) {
        TeacherDto teacher = teacherService.createTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(teacher, "Teacher created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "TEACHERS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<TeacherDto>> updateTeacher(
            @PathVariable UUID id,
            @Valid @RequestBody TeacherRequest request
    ) {
        TeacherDto teacher = teacherService.updateTeacher(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(teacher, "Teacher updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "TEACHERS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(@PathVariable UUID id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok(
                ApiResponse.success("Teacher deleted successfully")
        );
    }
}
