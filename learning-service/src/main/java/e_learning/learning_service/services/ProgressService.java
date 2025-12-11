package e_learning.learning_service.services;

import e_learning.learning_service.dto.*;
import e_learning.learning_service.entities.*;
import e_learning.learning_service.exceptions.*;
import e_learning.learning_service.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public ProgressService(LessonProgressRepository lessonProgressRepository,
            EnrollmentRepository enrollmentRepository,
            QuizAttemptRepository quizAttemptRepository) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @Transactional
    public ProgressResponse markLessonAsComplete(LessonProgressRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new EnrollmentNotFoundException(request.getEnrollmentId()));

        LessonProgress progress = lessonProgressRepository.findByEnrollmentIdAndLessonId(
                request.getEnrollmentId(), request.getLessonId())
                .orElse(LessonProgress.builder()
                        .enrollment(enrollment)
                        .lessonId(request.getLessonId())
                        .build());

        if (!progress.isCompleted()) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            lessonProgressRepository.save(progress);
        }

        return ProgressResponse.builder()
                .id(progress.getId())
                .enrollmentId(enrollment.getId())
                .lessonId(progress.getLessonId())
                .completed(progress.isCompleted())
                .completedAt(progress.getCompletedAt())
                .build();
    }

    @Transactional
    public QuizResult submitQuiz(QuizSubmission submission) {
        Enrollment enrollment = enrollmentRepository.findById(submission.getEnrollmentId())
                .orElseThrow(() -> new EnrollmentNotFoundException(submission.getEnrollmentId()));

        // Simulation of quiz scoring (mock implementation)
        // In production, we'd fetch correct answers from Catalog Service
        int score = 85;
        int totalQuestions = submission.getAnswers().size();
        boolean passed = true;

        QuizAttempt attempt = QuizAttempt.builder()
                .enrollment(enrollment)
                .quizId(submission.getQuizId())
                .score(score)
                .totalQuesions(totalQuestions)
                .passed(passed)
                .attemptedAt(LocalDateTime.now())
                .build();

        attempt = quizAttemptRepository.save(attempt);

        return QuizResult.builder()
                .attemptId(attempt.getId())
                .quizId(attempt.getQuizId())
                .score(attempt.getScore())
                .totalQuestions(attempt.getTotalQuesions())
                .passed(attempt.isPassed())
                .attemptedAt(attempt.getAttemptedAt())
                .build();
    }
}
