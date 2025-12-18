package e_learning.student_service.controllers;

import e_learning.student_service.dto.*;
import e_learning.student_service.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@Tag(name = "Students", description = "Student profile management APIs")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @Operation(summary = "Create a new student profile")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a student profile")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student profile")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a student by ID")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/auth/{authId}")
    @Operation(summary = "Get a student by Auth ID")
    public ResponseEntity<StudentResponse> getStudentByAuthId(@PathVariable Long authId) {
        return ResponseEntity.ok(studentService.getStudentByAuthId(authId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current student's profile (uses X-User-Id header from gateway)")
    public ResponseEntity<StudentResponse> getCurrentStudent(
            @RequestHeader(value = "X-User-Id", required = false) Long authId) {
        if (authId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(studentService.getStudentByAuthId(authId));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get a student by email")
    public ResponseEntity<StudentResponse> getStudentByEmail(@PathVariable String email) {
        return ResponseEntity.ok(studentService.getStudentByEmail(email));
    }

    @GetMapping
    @Operation(summary = "Get all students (paginated)")
    public ResponseEntity<Page<StudentResponse>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(studentService.getAllStudents(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all students (no pagination)")
    public ResponseEntity<List<StudentResponse>> getAllStudentsNoPagination() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }
}
