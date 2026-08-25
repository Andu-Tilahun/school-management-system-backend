package com.schoolmanagment.userservice.resource.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.userservice.resource.dto.ResourceDto;
import com.schoolmanagment.userservice.resource.dto.ResourceRequest;
import com.schoolmanagment.userservice.resource.service.ResourceService;
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

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/users/resources", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @RequiresPermission(resource = "RBAC_RESOURCES", scope = "CREATE")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ResourceRequest request) {
        ResourceDto resource = resourceService.create(request);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(resource)
                .message("Resource created successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @RequiresPermission(resource = "RBAC_RESOURCES", scope = "READ")
    public ResponseEntity<ApiResponse<Page>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ResourceDto> resources = resourceService.getAll(pageable);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(resources)
                .message("Resources retrieved successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "RBAC_RESOURCES", scope = "READ")
    public ResponseEntity<ApiResponse> getById(@PathVariable UUID id) {
        ResourceDto resource = resourceService.getById(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(resource)
                .message("Resource retrieved successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "RBAC_RESOURCES", scope = "UPDATE")
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @Valid @RequestBody ResourceRequest request) {
        ResourceDto resource = resourceService.update(id, request);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(resource)
                .message("Resource updated successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "RBAC_RESOURCES", scope = "DELETE")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        resourceService.delete(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Resource deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
