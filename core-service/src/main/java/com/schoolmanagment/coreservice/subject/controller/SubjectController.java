package com.schoolmanagment.coreservice.subject.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.coreservice.subject.dto.SubjectDto;
import com.schoolmanagment.coreservice.subject.dto.SubjectRequest;
import com.schoolmanagment.coreservice.subject.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor

public class SubjectController {
    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<SubjectDto> create(@Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.createSubject(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectDto> getById(@PathVariable UUID id){
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    @GetMapping
    public ResponseEntity<List<SubjectDto>>getAll(){
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDto> update(@PathVariable UUID id, @Valid @RequestBody SubjectRequest request){
        return ResponseEntity.ok(subjectService.updateSubject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        subjectService.deleteSubject(id);
        ApiResponse response = ApiResponse.success("Subject deleted successfully", null);
        return ResponseEntity.ok(response);

    }


}
