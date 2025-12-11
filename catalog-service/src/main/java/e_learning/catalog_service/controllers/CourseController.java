package e_learning.catalog_service.controllers;

import e_learning.catalog_service.dto.*;
import e_learning.catalog_service.entities.CourseLevel;
import e_learning.catalog_service.entities.CourseStatus;
import e_learning.catalog_service.services.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@Tag(name = "Courses", description = "Course management APIs")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @Operation(summary = "Create a new course")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing course")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a course by ID with all details")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping
    @Operation(summary = "Get all courses (paginated)")
    public ResponseEntity<Page<CourseSummaryResponse>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @GetMapping("/published")
    @Operation(summary = "Get all published courses (public)")
    public ResponseEntity<Page<CourseSummaryResponse>> getPublishedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(courseService.getPublishedCourses(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses with filters")
    public ResponseEntity<Page<CourseSummaryResponse>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(courseService.searchCourses(keyword, categoryId, level, status, pageable));
    }

    @GetMapping("/professor/{professorId}")
    @Operation(summary = "Get courses by professor")
    public ResponseEntity<List<CourseSummaryResponse>> getCoursesByProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(courseService.getCoursesByProfessor(professorId));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get courses by category")
    public ResponseEntity<Page<CourseSummaryResponse>> getCoursesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(courseService.getCoursesByCategory(categoryId, pageable));
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish a course")
    public ResponseEntity<CourseResponse> publishCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.publishCourse(id));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Archive a course")
    public ResponseEntity<CourseResponse> archiveCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.archiveCourse(id));
    }
}
