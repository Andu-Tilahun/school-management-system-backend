package com.schoolmanagment.coreservice.tenant.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.tenant.dto.TenantDto;
import com.schoolmanagment.coreservice.tenant.dto.TenantRequest;
import com.schoolmanagment.coreservice.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/tenants", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    @RequiresPermission(resource = "TENANTS", scope = "READ")
    public ResponseEntity<ApiResponse<List<TenantDto>>> getAllTenants() {
        List<TenantDto> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(
                ApiResponse.success(tenants, "Tenants retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "TENANTS", scope = "READ")
    public ResponseEntity<ApiResponse<TenantDto>> getTenantById(@PathVariable UUID id) {
        TenantDto tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(
                ApiResponse.success(tenant, "Tenant retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "TENANTS", scope = "CREATE")
    public ResponseEntity<ApiResponse<TenantDto>> createTenant(
            @Valid @RequestBody TenantRequest request
    ) {
        TenantDto tenant = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(tenant, "Tenant created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "TENANTS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<TenantDto>> updateTenant(
            @PathVariable UUID id,
            @Valid @RequestBody TenantRequest request
    ) {
        TenantDto tenant = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(tenant, "Tenant updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "TENANTS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(
                ApiResponse.success("Tenant deleted successfully")
        );
    }
}