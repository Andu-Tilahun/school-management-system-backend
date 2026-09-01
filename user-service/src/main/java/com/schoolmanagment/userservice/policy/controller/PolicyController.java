package com.schoolmanagment.userservice.policy.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.userservice.policy.dto.PolicyDto;
import com.schoolmanagment.userservice.policy.dto.PolicyRequest;
import com.schoolmanagment.userservice.policy.service.PolicyService;
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
@RequestMapping(value = "/api/users/policies", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    @RequiresPermission(resource = "POLICIES", scope = "CREATE")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody PolicyRequest request) {
        PolicyDto policy = policyService.create(request);

        ApiResponse<PolicyDto> response =
                ApiResponse.success(policy, "Policy created successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @RequiresPermission(resource = "POLICIES", scope = "READ")
    public ResponseEntity<ApiResponse<Page>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PolicyDto> policies = policyService.getAll(pageable);
        return ResponseEntity.ok(
                ApiResponse.success(policies, "Policies retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "POLICIES", scope = "READ")
    public ResponseEntity<ApiResponse> getById(@PathVariable UUID id) {
        PolicyDto policy = policyService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success(policy, "Policy retrieved successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "POLICIES", scope = "UPDATE")
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @Valid @RequestBody PolicyRequest request) {
        PolicyDto policy = policyService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(policy, "Policy updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "POLICIES", scope = "DELETE")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        policyService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Policy deleted successfully")
        );
    }
}
