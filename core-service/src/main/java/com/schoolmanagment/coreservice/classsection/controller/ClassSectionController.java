package com.schoolmanagment.coreservice.classsection.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionDto;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionFilterRequest;
import com.schoolmanagment.coreservice.classsection.dto.ClassSectionRequest;
import com.schoolmanagment.coreservice.classsection.service.ClassSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/class-sections", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ClassSectionController {

    private final ClassSectionService classSectionService;

    @GetMapping
    @RequiresPermission(resource = "CLASS_SECTIONS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<ClassSectionDto>>> getAllClassSections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ClassSectionDto> classSections = classSectionService.getAllClassSections(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(classSections, "Class sections retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "CLASS_SECTIONS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<ClassSectionDto>>> filterClassSections(
            @Valid @RequestBody ClassSectionFilterRequest request
    ) {
        Page<ClassSectionDto> classSections = classSectionService.filterClassSections(request);
        return ResponseEntity.ok(
                ApiResponse.success(classSections, "Class sections retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "CLASS_SECTIONS", scope = "READ")
    public ResponseEntity<ApiResponse<ClassSectionDto>> getClassSectionById(@PathVariable UUID id) {
        ClassSectionDto classSection = classSectionService.getClassSectionById(id);
        return ResponseEntity.ok(
                ApiResponse.success(classSection, "Class section retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "CLASS_SECTIONS", scope = "CREATE")
    public ResponseEntity<ApiResponse<ClassSectionDto>> createClassSection(
            @Valid @RequestBody ClassSectionRequest request
    ) {
        ClassSectionDto classSection = classSectionService.createClassSection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(classSection, "Class section created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "CLASS_SECTIONS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<ClassSectionDto>> updateClassSection(
            @PathVariable UUID id,
            @Valid @RequestBody ClassSectionRequest request
    ) {
        ClassSectionDto classSection = classSectionService.updateClassSection(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(classSection, "Class section updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "CLASS_SECTIONS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteClassSection(@PathVariable UUID id) {
        classSectionService.deleteClassSection(id);
        return ResponseEntity.ok(
                ApiResponse.success("Class section deleted successfully")
        );
    }
}
