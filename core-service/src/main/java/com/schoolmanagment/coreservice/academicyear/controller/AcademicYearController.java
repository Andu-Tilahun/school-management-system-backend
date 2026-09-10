package com.schoolmanagment.coreservice.academicyear.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearDto;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearFilterRequest;
import com.schoolmanagment.coreservice.academicyear.dto.AcademicYearRequest;
import com.schoolmanagment.coreservice.academicyear.service.AcademicYearService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/academic-years", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    @GetMapping
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<AcademicYearDto>>> getAllAcademicYears(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AcademicYearDto> academicYears = academicYearService.getAllAcademicYears(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(academicYears, "Academic years retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<AcademicYearDto>>> filterAcademicYears(
            @Valid @RequestBody AcademicYearFilterRequest request
    ) {
        Page<AcademicYearDto> academicYears = academicYearService.filterAcademicYears(request);
        return ResponseEntity.ok(
                ApiResponse.success(academicYears, "Academic years retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "READ")
    public ResponseEntity<ApiResponse<AcademicYearDto>> getAcademicYearById(@PathVariable UUID id) {
        AcademicYearDto academicYear = academicYearService.getAcademicYearById(id);
        return ResponseEntity.ok(
                ApiResponse.success(academicYear, "Academic year retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "CREATE")
    public ResponseEntity<ApiResponse<AcademicYearDto>> createAcademicYear(
            @Valid @RequestBody AcademicYearRequest request
    ) {
        AcademicYearDto academicYear = academicYearService.createAcademicYear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(academicYear, "Academic year created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<AcademicYearDto>> updateAcademicYear(
            @PathVariable UUID id,
            @Valid @RequestBody AcademicYearRequest request
    ) {
        AcademicYearDto academicYear = academicYearService.updateAcademicYear(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(academicYear, "Academic year updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteAcademicYear(@PathVariable UUID id) {
        academicYearService.deleteAcademicYear(id);
        return ResponseEntity.ok(
                ApiResponse.success("Academic year deleted successfully")
        );
    }
}
