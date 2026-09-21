package com.schoolmanagment.coreservice.penalty.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleDto;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleFilterRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRuleRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyTriggerDto;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import com.schoolmanagment.coreservice.penalty.service.PenaltyRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/penalties", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PenaltyRuleController {

    private final PenaltyRuleService penaltyRuleService;

    @GetMapping
    @RequiresPermission(resource = "PENALTY", scope = "READ")
    public ResponseEntity<ApiResponse<Page<PenaltyRuleDto>>> getAllPenaltyRules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PenaltyRuleDto> penaltyRules = penaltyRuleService.getAllPenaltyRules(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(penaltyRules, "Penalties retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "PENALTY", scope = "READ")
    public ResponseEntity<ApiResponse<Page<PenaltyRuleDto>>> filterPenaltyRules(
            @Valid @RequestBody PenaltyRuleFilterRequest request
    ) {
        Page<PenaltyRuleDto> penaltyRules = penaltyRuleService.filterPenaltyRules(request);
        return ResponseEntity.ok(
                ApiResponse.success(penaltyRules, "Penalties retrieved successfully")
        );
    }

    @GetMapping("/triggers")
    @RequiresPermission(resource = "PENALTY", scope = "READ")
    public ResponseEntity<ApiResponse<List<PenaltyTriggerDto>>> getPenaltyTriggers(
            @RequestParam(required = false) SourceModule sourceModule
    ) {
        List<PenaltyTriggerDto> triggers = penaltyRuleService.getPenaltyTriggers(sourceModule);
        return ResponseEntity.ok(
                ApiResponse.success(triggers, "Penalty triggers retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "PENALTY", scope = "READ")
    public ResponseEntity<ApiResponse<PenaltyRuleDto>> getPenaltyRuleById(@PathVariable UUID id) {
        PenaltyRuleDto penaltyRule = penaltyRuleService.getPenaltyRuleById(id);
        return ResponseEntity.ok(
                ApiResponse.success(penaltyRule, "Penalty retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "PENALTY", scope = "CREATE")
    public ResponseEntity<ApiResponse<PenaltyRuleDto>> createPenaltyRule(
            @Valid @RequestBody PenaltyRuleRequest request
    ) {
        PenaltyRuleDto penaltyRule = penaltyRuleService.createPenaltyRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(penaltyRule, "Penalty created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "PENALTY", scope = "UPDATE")
    public ResponseEntity<ApiResponse<PenaltyRuleDto>> updatePenaltyRule(
            @PathVariable UUID id,
            @Valid @RequestBody PenaltyRuleRequest request
    ) {
        PenaltyRuleDto penaltyRule = penaltyRuleService.updatePenaltyRule(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(penaltyRule, "Penalty updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "PENALTY", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deletePenaltyRule(@PathVariable UUID id) {
        penaltyRuleService.deletePenaltyRule(id);
        return ResponseEntity.ok(
                ApiResponse.success("Penalty deleted successfully")
        );
    }
}
