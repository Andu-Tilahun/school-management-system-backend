package com.schoolmanagment.coreservice.exam.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import com.schoolmanagment.coreservice.exam.service.StudentMarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/core/student-marks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StudentMarkController {

    private final StudentMarkService studentMarkService;

    @PostMapping
    @RequiresPermission(resource = "STUDENT_MARKS", scope = "CREATE")
    public ResponseEntity<ApiResponse<StudentMarkDto>> register(
            @Valid @RequestBody StudentMarkRequest request
    ) {
        StudentMarkDto studentMark = studentMarkService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(studentMark, "Student mark registered successfully")
        );
    }
}
