package com.schoolmanagment.coreservice.academicyear.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.academicyear.dto.TermDto;
import com.schoolmanagment.coreservice.academicyear.dto.TermRequest;
import com.schoolmanagment.coreservice.academicyear.service.TermService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/terms", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;

    @GetMapping
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<TermDto>>> getAllTerms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID academicYearId
    ) {
        Page<TermDto> terms = termService.getAllTerms(page, size, academicYearId);
        return ResponseEntity.ok(
                ApiResponse.success(terms, "Terms retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "READ")
    public ResponseEntity<ApiResponse<TermDto>> getTermById(@PathVariable UUID id) {
        TermDto term = termService.getTermById(id);
        return ResponseEntity.ok(
                ApiResponse.success(term, "Term retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "CREATE")
    public ResponseEntity<ApiResponse<TermDto>> createTerm(@Valid @RequestBody TermRequest request) {
        TermDto term = termService.createTerm(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(term, "Term created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<TermDto>> updateTerm(
            @PathVariable UUID id,
            @Valid @RequestBody TermRequest request
    ) {
        TermDto term = termService.updateTerm(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(term, "Term updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "ACADEMIC_YEARS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteTerm(@PathVariable UUID id) {
        termService.deleteTerm(id);
        return ResponseEntity.ok(
                ApiResponse.success("Term deleted successfully")
        );
    }
}
