package com.schoolmanagment.coreservice.student.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.student.dto.StudentFilterRequest;
import com.schoolmanagment.coreservice.student.dto.StudentRequest;
import com.schoolmanagment.coreservice.student.service.StudentService;
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
@RequestMapping(value = "/api/core/students", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

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

    @GetMapping("/by-timetable")
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getStudentsByTimetable() {
        List<StudentDto> students = studentService.getStudentsByTimetable();
        return ResponseEntity.ok(
                ApiResponse.success(students, "Students retrieved successfully")
        );
    }

    @GetMapping("/by-homeroom")
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getStudentsByHomeroom() {
        List<StudentDto> students = studentService.getStudentsByHomeroom();
        return ResponseEntity.ok(
                ApiResponse.success(students, "Students retrieved successfully")
        );
    }

    @GetMapping("/class-section/{classSectionId}")
    @RequiresPermission(resource = "STUDENTS", scope = "READ")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getStudentsByClassSection(
            @PathVariable UUID classSectionId) {
        List<StudentDto> students = studentService.getStudentsByClassSection(classSectionId);
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
}
