package com.schoolmanagment.userservice.scope.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.userservice.scope.dto.ScopeDto;
import com.schoolmanagment.userservice.scope.dto.ScopeRequest;
import com.schoolmanagment.userservice.scope.service.ScopeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/users/scopes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ScopeController {

    private final ScopeService scopeService;

    @PostMapping
    @RequiresPermission(resource = "SCOPES", scope = "CREATE")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ScopeRequest request) {
        ScopeDto scope = scopeService.create(request);
        ApiResponse<ScopeDto> response =
                ApiResponse.success(scope, "Scope created successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @RequiresPermission(resource = "SCOPES", scope = "READ")
    public ResponseEntity<ApiResponse<Page>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ScopeDto> scopes = scopeService.getAll(pageable);
        return ResponseEntity.ok(
                ApiResponse.success(scopes, "Scopes retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "SCOPES", scope = "READ")
    public ResponseEntity<ApiResponse> getById(@PathVariable UUID id) {
        ScopeDto scope = scopeService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success(scope, "Scope retrieved successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "SCOPES", scope = "UPDATE")
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @Valid @RequestBody ScopeRequest request) {
        ScopeDto scope = scopeService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(scope, "Scope updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "SCOPES", scope = "DELETE")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        scopeService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Scope deleted successfully")
        );
    }
}
