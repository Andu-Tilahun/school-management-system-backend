package com.schoolmanagment.coreservice.timetable.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.timetable.dto.TimetableDto;
import com.schoolmanagment.coreservice.timetable.dto.TimetableFilterRequest;
import com.schoolmanagment.coreservice.timetable.dto.TimetableRequest;
import com.schoolmanagment.coreservice.timetable.service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/timetables", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @GetMapping
    @RequiresPermission(resource = "TIMETABLES", scope = "READ")
    public ResponseEntity<ApiResponse<Page<TimetableDto>>> getAllTimetables(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TimetableDto> timetables = timetableService.getAllTimetables(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(timetables, "Timetables retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "TIMETABLES", scope = "READ")
    public ResponseEntity<ApiResponse<Page<TimetableDto>>> filterTimetables(
            @Valid @RequestBody TimetableFilterRequest request
    ) {
        Page<TimetableDto> timetables = timetableService.filterTimetables(request);
        return ResponseEntity.ok(
                ApiResponse.success(timetables, "Timetables retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "TIMETABLES", scope = "READ")
    public ResponseEntity<ApiResponse<TimetableDto>> getTimetableById(@PathVariable UUID id) {
        TimetableDto timetable = timetableService.getTimetableById(id);
        return ResponseEntity.ok(
                ApiResponse.success(timetable, "Timetable retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "TIMETABLES", scope = "CREATE")
    public ResponseEntity<ApiResponse<TimetableDto>> createTimetable(
            @Valid @RequestBody TimetableRequest request
    ) {
        TimetableDto timetable = timetableService.createTimetable(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(timetable, "Timetable created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "TIMETABLES", scope = "UPDATE")
    public ResponseEntity<ApiResponse<TimetableDto>> updateTimetable(
            @PathVariable UUID id,
            @Valid @RequestBody TimetableRequest request
    ) {
        TimetableDto timetable = timetableService.updateTimetable(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(timetable, "Timetable updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "TIMETABLES", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteTimetable(@PathVariable UUID id) {
        timetableService.deleteTimetable(id);
        return ResponseEntity.ok(
                ApiResponse.success("Timetable deleted successfully")
        );
    }
}
