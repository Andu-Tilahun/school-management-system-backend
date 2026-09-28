package com.schoolmanagment.coreservice.exam.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkDto;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkFilterRequest;
import com.schoolmanagment.coreservice.exam.dto.StudentMarkRequest;
import com.schoolmanagment.coreservice.exam.service.StudentMarkService;
import com.schoolmanagment.coreservice.student.dto.StudentDto;
import com.schoolmanagment.coreservice.teacher.dto.TeacherSubjectAssignmentDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/core/student-marks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StudentMarkController {

    private final StudentMarkService studentMarkService;

    @GetMapping("/subjects")
    @RequiresPermission(resource = "STUDENT_MARKS", scope = "READ")
    public ResponseEntity<ApiResponse<List<TeacherSubjectAssignmentDto>>> getSubjectsForCurrentTeacher() {
        List<TeacherSubjectAssignmentDto> subjects = studentMarkService.getSubjectsForCurrentTeacher();
        return ResponseEntity.ok(
                ApiResponse.success(subjects, "Subjects retrieved successfully")
        );
    }

    @GetMapping("/my-subjects")
    @RequiresPermission(resource = "STUDENT_MARKS", scope = "READ")
    public ResponseEntity<ApiResponse<List<TeacherSubjectAssignmentDto>>> getSubjectsForCurrentStudent() {
        List<TeacherSubjectAssignmentDto> subjects = studentMarkService.getSubjectsForCurrentStudent();
        return ResponseEntity.ok(
                ApiResponse.success(subjects, "Subjects retrieved successfully")
        );
    }

    @GetMapping("/my-students")
    @RequiresPermission(resource = "STUDENT_MARKS", scope = "READ")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getStudentsForCurrentEmergencyContact() {
        List<StudentDto> students = studentMarkService.getStudentsForCurrentEmergencyContact();
        return ResponseEntity.ok(
                ApiResponse.success(students, "Students retrieved successfully")
        );
    }

    @GetMapping("/students/{studentId}/subjects")
    @RequiresPermission(resource = "STUDENT_MARKS", scope = "READ")
    public ResponseEntity<ApiResponse<List<TeacherSubjectAssignmentDto>>> getSubjectsForEmergencyContactStudent(
            @PathVariable UUID studentId
    ) {
        List<TeacherSubjectAssignmentDto> subjects = studentMarkService.getSubjectsForEmergencyContactStudent(studentId);
        return ResponseEntity.ok(
                ApiResponse.success(subjects, "Subjects retrieved successfully")
        );
    }

    @PostMapping("/filter")
    @RequiresPermission(resource = "STUDENT_MARKS", scope = "READ")
    public ResponseEntity<ApiResponse<Page<StudentMarkDto>>> filter(
            @Valid @RequestBody StudentMarkFilterRequest request
    ) {
        Page<StudentMarkDto> studentMarks = studentMarkService.filter(request);
        return ResponseEntity.ok(
                ApiResponse.success(studentMarks, "Student marks retrieved successfully")
        );
    }

    @PostMapping
    @RequiresPermission(resource = "STUDENT_MARKS", scope = "CREATE")
    public ResponseEntity<ApiResponse<StudentMarkDto>> register(
            @Valid @RequestBody StudentMarkRequest request
    ) {
        StudentMarkDto studentMark = studentMarkService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(studentMark, "Student mark registered successfully")
        );
    }
}
