package com.schoolmanagment.coreservice.grade.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.grade.dto.GradeDto;
import com.schoolmanagment.coreservice.grade.dto.GradeFilterRequest;
import com.schoolmanagment.coreservice.grade.dto.GradeRequest;
import com.schoolmanagment.coreservice.grade.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/grades", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    @RequiresPermission(resource = "GRADES", scope = "READ")
    public ResponseEntity<ApiResponse<Page<GradeDto>>> getAllGrades(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<GradeDto> grades = gradeService.getAllGrades(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(grades, "Grades retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "GRADES", scope = "READ")
    public ResponseEntity<ApiResponse<Page<GradeDto>>> filterGrades(
            @Valid @RequestBody GradeFilterRequest request
    ) {
        Page<GradeDto> grades = gradeService.filterGrades(request);
        return ResponseEntity.ok(
                ApiResponse.success(grades, "Grades retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "GRADES", scope = "READ")
    public ResponseEntity<ApiResponse<GradeDto>> getGradeById(@PathVariable UUID id) {
        GradeDto grade = gradeService.getGradeById(id);
        return ResponseEntity.ok(
                ApiResponse.success(grade, "Grade retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "GRADES", scope = "CREATE")
    public ResponseEntity<ApiResponse<GradeDto>> createGrade(
            @Valid @RequestBody GradeRequest request
    ) {
        GradeDto grade = gradeService.createGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(grade, "Grade created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "GRADES", scope = "UPDATE")
    public ResponseEntity<ApiResponse<GradeDto>> updateGrade(
            @PathVariable UUID id,
            @Valid @RequestBody GradeRequest request
    ) {
        GradeDto grade = gradeService.updateGrade(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(grade, "Grade updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "GRADES", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteGrade(@PathVariable UUID id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.ok(
                ApiResponse.success("Grade deleted successfully")
        );
    }
}
