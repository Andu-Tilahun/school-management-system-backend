package com.schoolmanagment.coreservice.attendance.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceDto;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceFilterRequest;
import com.schoolmanagment.coreservice.attendance.dto.AttendanceRequest;
import com.schoolmanagment.coreservice.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/attendances", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    @RequiresPermission(resource = "ATTENDANCE", scope = "READ")
    public ResponseEntity<ApiResponse<Page<AttendanceDto>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AttendanceDto> records = attendanceService.list(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(records, "Attendances retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "ATTENDANCE", scope = "READ")
    public ResponseEntity<ApiResponse<Page<AttendanceDto>>> filter(
            @Valid @RequestBody AttendanceFilterRequest request
    ) {
        Page<AttendanceDto> records = attendanceService.filter(request);
        return ResponseEntity.ok(
                ApiResponse.success(records, "Attendances retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "ATTENDANCE", scope = "READ")
    public ResponseEntity<ApiResponse<AttendanceDto>> getById(@PathVariable UUID id) {
        AttendanceDto record = attendanceService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success(record, "Attendance retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "ATTENDANCE", scope = "CREATE")
    public ResponseEntity<ApiResponse<AttendanceDto>> create(
            @Valid @RequestBody AttendanceRequest request
    ) {
        AttendanceDto record = attendanceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(record, "Attendance created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "ATTENDANCE", scope = "UPDATE")
    public ResponseEntity<ApiResponse<AttendanceDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceRequest request
    ) {
        AttendanceDto record = attendanceService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(record, "Attendance updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "ATTENDANCE", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        attendanceService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Attendance deleted successfully")
        );
    }
}
