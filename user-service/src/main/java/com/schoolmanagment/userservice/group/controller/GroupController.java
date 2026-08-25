package com.schoolmanagment.userservice.group.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.userservice.group.dto.GroupDto;
import com.schoolmanagment.userservice.group.dto.GroupRequest;
import com.schoolmanagment.userservice.group.service.GroupService;
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
@RequestMapping(value = "/api/users/groups", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @RequiresPermission(resource = "GROUPS", scope = "CREATE")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody GroupRequest request) {
        GroupDto group = groupService.create(request);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(group)
                .message("Group created successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @RequiresPermission(resource = "GROUPS", scope = "READ")
    public ResponseEntity<ApiResponse<Page>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<GroupDto> groups = groupService.getAll(pageable);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(groups)
                .message("Groups retrieved successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "GROUPS", scope = "READ")
    public ResponseEntity<ApiResponse> getById(@PathVariable UUID id) {
        GroupDto group = groupService.getById(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(group)
                .message("Group retrieved successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "GROUPS", scope = "UPDATE")
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @Valid @RequestBody GroupRequest request) {
        GroupDto group = groupService.update(id, request);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(group)
                .message("Group updated successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "GROUPS", scope = "DELETE")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        groupService.delete(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Group deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
