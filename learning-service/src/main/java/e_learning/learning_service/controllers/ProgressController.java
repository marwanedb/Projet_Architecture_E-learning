package e_learning.learning_service.controllers;

import e_learning.learning_service.dto.LessonProgressRequest;
import e_learning.learning_service.dto.ProgressResponse;
import e_learning.learning_service.dto.QuizResult;
import e_learning.learning_service.dto.QuizSubmission;
import e_learning.learning_service.services.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/progress")
@Tag(name = "Progress", description = "Course progress tracking")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/lessons")
    @Operation(summary = "Mark a lesson as completed")
    public ResponseEntity<ProgressResponse> markLessonComplete(@Valid @RequestBody LessonProgressRequest request) {
        return ResponseEntity.ok(progressService.markLessonAsComplete(request));
    }

    @PostMapping("/quizzes")
    @Operation(summary = "Submit a quiz attempt")
    public ResponseEntity<QuizResult> submitQuiz(@Valid @RequestBody QuizSubmission submission) {
        return ResponseEntity.ok(progressService.submitQuiz(submission));
    }
}
