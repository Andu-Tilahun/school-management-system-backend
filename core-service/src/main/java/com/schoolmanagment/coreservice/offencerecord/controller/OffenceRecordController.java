package com.schoolmanagment.coreservice.offencerecord.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordDto;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordFilterRequest;
import com.schoolmanagment.coreservice.offencerecord.dto.OffenceRecordRequest;
import com.schoolmanagment.coreservice.offencerecord.service.OffenceRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/offence-records", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OffenceRecordController {

    private final OffenceRecordService offenceRecordService;

    @GetMapping
    @RequiresPermission(resource = "OFFENCE_RECORDS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<OffenceRecordDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OffenceRecordDto> records = offenceRecordService.list(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(records, "Offence records retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "OFFENCE_RECORDS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<OffenceRecordDto>>> filter(
            @Valid @RequestBody OffenceRecordFilterRequest request
    ) {
        Page<OffenceRecordDto> records = offenceRecordService.filter(request);
        return ResponseEntity.ok(
                ApiResponse.success(records, "Offence records retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "OFFENCE_RECORDS", scope = "READ")
    public ResponseEntity<ApiResponse<OffenceRecordDto>> getById(@PathVariable UUID id) {
        OffenceRecordDto record = offenceRecordService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success(record, "Offence record retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "OFFENCE_RECORDS", scope = "CREATE")
    public ResponseEntity<ApiResponse<OffenceRecordDto>> create(
            @Valid @RequestBody OffenceRecordRequest request
    ) {
        OffenceRecordDto record = offenceRecordService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(record, "Offence record created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "OFFENCE_RECORDS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<OffenceRecordDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody OffenceRecordRequest request
    ) {
        OffenceRecordDto record = offenceRecordService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(record, "Offence record updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "OFFENCE_RECORDS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        offenceRecordService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Offence record deleted successfully")
        );
    }
}
