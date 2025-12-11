package e_learning.learning_service.controllers;

import e_learning.learning_service.dto.EnrollmentRequest;
import e_learning.learning_service.dto.EnrollmentResponse;
import e_learning.learning_service.services.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@Tag(name = "Enrollments", description = "Course enrollment management")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @Operation(summary = "Enroll a student in a course")
    public ResponseEntity<EnrollmentResponse> enrollStudent(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enrollStudent(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get enrollment details by ID")
    public ResponseEntity<EnrollmentResponse> getEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all enrollments for a student")
    public ResponseEntity<List<EnrollmentResponse>> getStudentEnrollments(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getStudentEnrollments(studentId));
    }
}