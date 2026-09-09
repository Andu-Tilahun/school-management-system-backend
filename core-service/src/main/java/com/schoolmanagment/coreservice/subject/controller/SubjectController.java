package com.schoolmanagment.coreservice.subject.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.subject.dto.SubjectDto;
import com.schoolmanagment.coreservice.subject.dto.SubjectFilterRequest;
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

    @GetMapping
    @RequiresPermission(resource = "SUBJECTS", scope = "READ")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getAllSubjects(SubjectFilterRequest filterRequest) {
        List<SubjectDto> subjects = subjectService.getSubjects(filterRequest);
        return ResponseEntity.ok(
                ApiResponse.success(subjects, "Subjects retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "SUBJECTS", scope = "READ")
    public ResponseEntity<ApiResponse<SubjectDto>> getSubjectById(@PathVariable UUID id) {
        SubjectDto subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(
                ApiResponse.success(subject, "Subject retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "SUBJECTS", scope = "CREATE")
    public ResponseEntity<ApiResponse<SubjectDto>> createSubject(
            @Valid @RequestBody SubjectRequest request
    ) {
        SubjectDto subject = subjectService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(subject, "Subject created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "SUBJECTS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<SubjectDto>> updateSubject(
            @PathVariable UUID id,
            @Valid @RequestBody SubjectRequest request
    ) {
        SubjectDto subject = subjectService.updateSubject(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(subject, "Subject updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "SUBJECTS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable UUID id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(
                ApiResponse.success("Subject deleted successfully")
        );
    }
}
