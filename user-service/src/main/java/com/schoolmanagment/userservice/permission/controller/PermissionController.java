package com.schoolmanagment.userservice.permission.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.userservice.permission.dto.PermissionDto;
import com.schoolmanagment.userservice.permission.dto.PermissionFilterRequest;
import com.schoolmanagment.userservice.permission.dto.PermissionRequest;
import com.schoolmanagment.userservice.permission.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/users/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @RequiresPermission(resource = "PERMISSIONS", scope = "CREATE")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody PermissionRequest request) {
        PermissionDto permission = permissionService.create(request);
        ApiResponse<PermissionDto> response =
                ApiResponse.success(permission, "Permission created successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @RequiresPermission(resource = "PERMISSIONS", scope = "READ")
    public ResponseEntity<ApiResponse<Page>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) UUID resourceId,
            @RequestParam(required = false) String searchText
    ) {
        PermissionFilterRequest filterRequest = PermissionFilterRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDir)
                .resourceId(resourceId)
                .searchText(searchText)
                .build();

        Page<PermissionDto> permissions = permissionService.filterPermissions(filterRequest);

        return ResponseEntity.ok(
                ApiResponse.success(permissions, "Permissions retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "PERMISSIONS", scope = "READ")
    public ResponseEntity<ApiResponse> getById(@PathVariable UUID id) {
        PermissionDto permission = permissionService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success(permission, "Permission retrieved successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "PERMISSIONS", scope = "UPDATE")
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @Valid @RequestBody PermissionRequest request) {
        PermissionDto permission = permissionService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(permission, "Permission updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "PERMISSIONS", scope = "DELETE")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        permissionService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Permission deleted successfully")
        );
    }
}
