package com.schoolmanagment.coreservice.penalty.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyDto;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyFilterRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyRequest;
import com.schoolmanagment.coreservice.penalty.dto.PenaltyTriggerDto;
import com.schoolmanagment.coreservice.penalty.enums.SourceModule;
import com.schoolmanagment.coreservice.penalty.service.PenaltyService;
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
public class PenaltyController {

    private final PenaltyService penaltyService;

    @GetMapping
    @RequiresPermission(resource = "PENALTY", scope = "READ")
    public ResponseEntity<ApiResponse<Page<PenaltyDto>>> getAllPenalties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PenaltyDto> penalties = penaltyService.getAllPenalties(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(penalties, "Penalties retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "PENALTY", scope = "READ")
    public ResponseEntity<ApiResponse<Page<PenaltyDto>>> filterPenalties(
            @Valid @RequestBody PenaltyFilterRequest request
    ) {
        Page<PenaltyDto> penalties = penaltyService.filterPenalties(request);
        return ResponseEntity.ok(
                ApiResponse.success(penalties, "Penalties retrieved successfully")
        );
    }

    @GetMapping("/triggers")
    @RequiresPermission(resource = "PENALTY", scope = "READ")
    public ResponseEntity<ApiResponse<List<PenaltyTriggerDto>>> getPenaltyTriggers(
            @RequestParam(required = false) SourceModule sourceModule
    ) {
        List<PenaltyTriggerDto> triggers = penaltyService.getPenaltyTriggers(sourceModule);
        return ResponseEntity.ok(
                ApiResponse.success(triggers, "Penalty triggers retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "PENALTY", scope = "READ")
    public ResponseEntity<ApiResponse<PenaltyDto>> getPenaltyById(@PathVariable UUID id) {
        PenaltyDto penalty = penaltyService.getPenaltyById(id);
        return ResponseEntity.ok(
                ApiResponse.success(penalty, "Penalty retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "PENALTY", scope = "CREATE")
    public ResponseEntity<ApiResponse<PenaltyDto>> createPenalty(
            @Valid @RequestBody PenaltyRequest request
    ) {
        PenaltyDto penalty = penaltyService.createPenalty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(penalty, "Penalty created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "PENALTY", scope = "UPDATE")
    public ResponseEntity<ApiResponse<PenaltyDto>> updatePenalty(
            @PathVariable UUID id,
            @Valid @RequestBody PenaltyRequest request
    ) {
        PenaltyDto penalty = penaltyService.updatePenalty(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(penalty, "Penalty updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "PENALTY", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deletePenalty(@PathVariable UUID id) {
        penaltyService.deletePenalty(id);
        return ResponseEntity.ok(
                ApiResponse.success("Penalty deleted successfully")
        );
    }
}
