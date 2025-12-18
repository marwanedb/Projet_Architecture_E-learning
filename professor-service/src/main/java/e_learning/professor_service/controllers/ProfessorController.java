package e_learning.professor_service.controllers;

import e_learning.professor_service.dto.*;
import e_learning.professor_service.services.ProfessorService;
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
@RequestMapping("/professors")
@Tag(name = "Professors", description = "Professor profile management APIs")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping
    @Operation(summary = "Create a new professor profile")
    public ResponseEntity<ProfessorResponse> createProfessor(@Valid @RequestBody CreateProfessorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(professorService.createProfessor(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a professor profile")
    public ResponseEntity<ProfessorResponse> updateProfessor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfessorRequest request) {
        return ResponseEntity.ok(professorService.updateProfessor(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a professor profile")
    public ResponseEntity<Void> deleteProfessor(@PathVariable Long id) {
        professorService.deleteProfessor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a professor by ID")
    public ResponseEntity<ProfessorResponse> getProfessorById(@PathVariable Long id) {
        return ResponseEntity.ok(professorService.getProfessorById(id));
    }

    @GetMapping("/auth/{authId}")
    @Operation(summary = "Get a professor by Auth ID")
    public ResponseEntity<ProfessorResponse> getProfessorByAuthId(@PathVariable Long authId) {
        return ResponseEntity.ok(professorService.getProfessorByAuthId(authId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current professor's profile (uses X-User-Id header from gateway)")
    public ResponseEntity<ProfessorResponse> getCurrentProfessor(
            @RequestHeader(value = "X-User-Id", required = false) Long authId) {
        if (authId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(professorService.getProfessorByAuthId(authId));
    }

    @GetMapping("/{id}/courses")
    @Operation(summary = "Get courses taught by professor")
    public ResponseEntity<List<Object>> getProfessorCourses(@PathVariable Long id) {
        return ResponseEntity.ok(professorService.getProfessorCourses(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get a professor by email")
    public ResponseEntity<ProfessorResponse> getProfessorByEmail(@PathVariable String email) {
        return ResponseEntity.ok(professorService.getProfessorByEmail(email));
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get professors by department")
    public ResponseEntity<List<ProfessorResponse>> getProfessorsByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(professorService.getProfessorsByDepartment(department));
    }

    @GetMapping
    @Operation(summary = "Get all professors (paginated)")
    public ResponseEntity<Page<ProfessorResponse>> getAllProfessors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(professorService.getAllProfessors(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all professors (no pagination)")
    public ResponseEntity<List<ProfessorResponse>> getAllProfessorsNoPagination() {
        return ResponseEntity.ok(professorService.getAllProfessors());
    }
}
