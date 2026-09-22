package com.schoolmanagment.coreservice.exam.controller;

import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import com.schoolmanagment.coreservice.exam.service.StudentMarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student-marks")
@RequiredArgsConstructor
public class StudentMarkController {

    private final StudentMarkService studentMarkService;

    @PostMapping
    public ResponseEntity<StudentMarkDto> register(@Valid @RequestBody StudentMarkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentMarkService.register(request));
    }

}
