package com.schoolmanagment.coreservice.student.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.student.dto.EmergencyContactDto;
import com.schoolmanagment.coreservice.student.dto.EmergencyContactRequest;
import com.schoolmanagment.coreservice.student.service.EmergencyContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/students", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;

    @GetMapping("/emergency-contacts/email")
    @RequiresPermission(resource = "STUDENTS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<EmergencyContactDto>> getEmergencyContactByEmail(
            @RequestParam String email
    ) {
        Optional<EmergencyContactDto> contact = emergencyContactService.findByEmail(email);

        return ResponseEntity.ok(
                contact.isPresent()
                        ? ApiResponse.success(contact.get(), "Existing emergency contact found")
                        : ApiResponse.success(null, "No emergency contact found for this email")
        );
    }

    @GetMapping("/{studentId}/emergency-contacts")
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<List<EmergencyContactDto>>> getAllEmergencyContacts(
            @PathVariable UUID studentId
    ) {
        List<EmergencyContactDto> contacts = emergencyContactService.getEmergencyContactsForStudent(studentId);
        return ResponseEntity.ok(
                ApiResponse.success(contacts, "Emergency contacts retrieved successfully")
        );
    }

    @PostMapping("/{studentId}/emergency-contacts")
    @RequiresPermission(resource = "STUDENTS", scope = "CREATE")
    public ResponseEntity<ApiResponse<EmergencyContactDto>> createEmergencyContact(
            @PathVariable UUID studentId,
            @Valid @RequestBody EmergencyContactRequest request
    ) {
        EmergencyContactDto contact = emergencyContactService.registerEmergencyContact(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(contact, "Emergency contact registered successfully")
        );
    }

    @DeleteMapping("/{studentId}/emergency-contacts/{emergencyContactId}")
    @RequiresPermission(resource = "STUDENTS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteEmergencyContact(
            @PathVariable UUID studentId,
            @PathVariable UUID emergencyContactId
    ) {
        emergencyContactService.removeEmergencyContactFromStudent(studentId, emergencyContactId);
        return ResponseEntity.ok(
                ApiResponse.success("Emergency contact removed from student successfully")
        );
    }

    @PutMapping("/emergency-contacts/{id}")
    @RequiresPermission(resource = "STUDENTS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<EmergencyContactDto>> updateEmergencyContact(
            @PathVariable UUID id,
            @Valid @RequestBody EmergencyContactRequest request
    ) {
        EmergencyContactDto contact = emergencyContactService.updateEmergencyContact(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(contact, "Emergency contact updated successfully")
        );
    }
}
