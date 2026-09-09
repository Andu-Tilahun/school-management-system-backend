package com.schoolmanagment.coreservice.school.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.school.dto.SchoolDto;
import com.schoolmanagment.coreservice.school.dto.SchoolRequest;
import com.schoolmanagment.coreservice.school.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/schools", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping
//    @RequiresPermission(resource = "SCHOOLS", scope = "READ")
    public ResponseEntity<ApiResponse<List<SchoolDto>>> getAllSchools() {
        List<SchoolDto> schools = schoolService.getAllSchools();
        return ResponseEntity.ok(ApiResponse.success(schools, "Schools retrieved successfully"));
    }

    @GetMapping("/{id}")
//    @RequiresPermission(resource = "SCHOOLS", scope = "READ")
    public ResponseEntity<ApiResponse<SchoolDto>> getSchoolById(@PathVariable UUID id) {
        SchoolDto school = schoolService.getSchoolById(id);
        return ResponseEntity.ok(ApiResponse.success(school, "School retrieved successfully"));
    }

    @PostMapping
//    @RequiresPermission(resource = "SCHOOLS", scope = "CREATE")
    public ResponseEntity<ApiResponse<SchoolDto>> createSchool(@Valid @RequestBody SchoolRequest request) {
        SchoolDto school = schoolService.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(school, "School created successfully"));
    }

    @PutMapping("/{id}")
//    @RequiresPermission(resource = "SCHOOLS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<SchoolDto>> updateSchool(
            @PathVariable UUID id, @Valid @RequestBody SchoolRequest request) {
        SchoolDto school = schoolService.updateSchool(id, request);
        return ResponseEntity.ok(ApiResponse.success(school, "School updated successfully"));
    }

    @DeleteMapping("/{id}")
//    @RequiresPermission(resource = "SCHOOLS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteSchool(@PathVariable UUID id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.ok(ApiResponse.success("School deleted successfully"));
    }
}