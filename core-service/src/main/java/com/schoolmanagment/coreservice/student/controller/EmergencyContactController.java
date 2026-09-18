package com.schoolmanagment.coreservice.student.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.student.dto.EmergencyContactDto;
import com.schoolmanagment.coreservice.student.service.EmergencyContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/core/emergency-contacts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;

    @GetMapping
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<EmergencyContactDto>> findByEmail(@RequestParam String email) {
        return emergencyContactService.findByEmail(email)
                .map(contact -> ResponseEntity.ok(
                        ApiResponse.success(contact, "Emergency contact retrieved successfully")))
                .orElseGet(() -> ResponseEntity.ok(
                        ApiResponse.success(null, "Emergency contact not found")));
    }
}
