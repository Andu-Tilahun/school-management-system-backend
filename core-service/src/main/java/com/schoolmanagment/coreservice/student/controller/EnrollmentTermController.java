package com.schoolmanagment.coreservice.student.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.student.dto.EnrollmentTermDto;
import com.schoolmanagment.coreservice.student.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/enrollment-terms", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EnrollmentTermController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<List<EnrollmentTermDto>>> list(
            @RequestParam(required = false) UUID enrollmentId,
            @RequestParam(required = false) UUID studentId
    ) {
        List<EnrollmentTermDto> terms = enrollmentService.listTerms(enrollmentId, studentId);
        return ResponseEntity.ok(
                ApiResponse.success(terms, "Enrollment terms retrieved successfully")
        );
    }
}
