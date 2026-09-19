package com.schoolmanagment.coreservice.student.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.student.dto.EnrollmentDto;
import com.schoolmanagment.coreservice.student.dto.EnrollmentFilterRequest;
import com.schoolmanagment.coreservice.student.dto.EnrollmentRequest;
import com.schoolmanagment.coreservice.student.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/students/{studentId}/enrollments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<EnrollmentDto>>> getStudentEnrollments(
            @PathVariable UUID studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        EnrollmentFilterRequest filter = EnrollmentFilterRequest.builder()
                .page(page)
                .size(size)
                .build();
        Page<EnrollmentDto> enrollments = enrollmentService.listByStudent(studentId, filter);
        return ResponseEntity.ok(
                ApiResponse.success(enrollments, "Enrollments retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<EnrollmentDto>>> filterStudentEnrollments(
            @PathVariable UUID studentId,
            @Valid @RequestBody EnrollmentFilterRequest request
    ) {
        Page<EnrollmentDto> enrollments = enrollmentService.listByStudent(studentId, request);
        return ResponseEntity.ok(
                ApiResponse.success(enrollments, "Enrollments retrieved successfully")
        );
    }

    @GetMapping("/{enrollmentId}")
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<EnrollmentDto>> getStudentEnrollmentById(
            @PathVariable UUID studentId,
            @PathVariable UUID enrollmentId
    ) {
        EnrollmentDto enrollment = enrollmentService.getById(studentId, enrollmentId);
        return ResponseEntity.ok(
                ApiResponse.success(enrollment, "Enrollment retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "STUDENTS", scope = "CREATE")
    public ResponseEntity<ApiResponse<EnrollmentDto>> createStudentEnrollment(
            @PathVariable UUID studentId,
            @Valid @RequestBody EnrollmentRequest request
    ) {
        EnrollmentDto enrollment = enrollmentService.create(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(enrollment, "Enrollment created successfully")
        );
    }

    @PutMapping("/{enrollmentId}")
    @RequiresPermission(resource = "STUDENTS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<EnrollmentDto>> updateStudentEnrollment(
            @PathVariable UUID studentId,
            @PathVariable UUID enrollmentId,
            @Valid @RequestBody EnrollmentRequest request
    ) {
        EnrollmentDto enrollment = enrollmentService.update(studentId, enrollmentId, request);
        return ResponseEntity.ok(
                ApiResponse.success(enrollment, "Enrollment updated successfully")
        );
    }

    @DeleteMapping("/{enrollmentId}")
    @RequiresPermission(resource = "STUDENTS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteStudentEnrollment(
            @PathVariable UUID studentId,
            @PathVariable UUID enrollmentId
    ) {
        enrollmentService.delete(studentId, enrollmentId);
        return ResponseEntity.ok(
                ApiResponse.success("Enrollment deleted successfully")
        );
    }
}
