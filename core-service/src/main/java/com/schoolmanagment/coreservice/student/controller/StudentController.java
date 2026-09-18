package com.schoolmanagment.coreservice.student.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.coreservice.student.dto.*;
import com.schoolmanagment.coreservice.student.service.EmergencyContactService;
import com.schoolmanagment.coreservice.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
public class StudentController {

    private final StudentService studentService;

    private final EmergencyContactService emergencyContactService;

    @GetMapping
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<StudentDto>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<StudentDto> students = studentService.getAllStudents(page, size);
        return ResponseEntity.ok(
                ApiResponse.success(students, "Students retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<StudentDto>>> filterStudents(
            @Valid @RequestBody StudentFilterRequest request
    ) {
        Page<StudentDto> students = studentService.filterStudents(request);
        return ResponseEntity.ok(
                ApiResponse.success(students, "Students retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<StudentDto>> getStudentById(@PathVariable UUID id) {
        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(
                ApiResponse.success(student, "Student retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "STUDENTS", scope = "CREATE")
    public ResponseEntity<ApiResponse<StudentDto>> createStudent(
            @Valid @RequestBody StudentRequest request
    ) {
        StudentDto student = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(student, "Student created successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "STUDENTS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<StudentDto>> updateStudent(
            @PathVariable UUID id,
            @Valid @RequestBody StudentRequest request
    ) {
        StudentDto student = studentService.updateStudent(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(student, "Student updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "STUDENTS", scope = "DELETE")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(
                ApiResponse.success("Student deleted successfully")
        );
    }

    @GetMapping("/emergency-contacts/lookup")
    @RequiresPermission(resource = "STUDENTS", scope = "UPDATE")
    public ResponseEntity<ApiResponse<EmergencyContactDto>> lookupByEmail(
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
    public ResponseEntity<ApiResponse<List<EmergencyContactDto>>> getEmergencyContactsForStudent(
            @PathVariable UUID studentId
    ) {
        List<EmergencyContactDto> contacts = emergencyContactService.getEmergencyContactsForStudent(studentId);
        return ResponseEntity.ok(
                ApiResponse.success(contacts, "Emergency contacts retrieved successfully")
        );
    }

    @PostMapping("/{studentId}/emergency-contacts")
    @RequiresPermission(resource = "STUDENTS", scope = "CREATE")
    public ResponseEntity<ApiResponse<EmergencyContactDto>> registerEmergencyContact(
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
    public ResponseEntity<ApiResponse<Void>> removeEmergencyContactFromStudent(
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
