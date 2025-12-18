package e_learning.learning_service.services;

import e_learning.learning_service.clients.CatalogClient;
import e_learning.learning_service.dto.*;
import e_learning.learning_service.entities.*;
import e_learning.learning_service.exceptions.*;
import e_learning.learning_service.repositories.*;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ProgressService {

        private final LessonProgressRepository lessonProgressRepository;
        private final EnrollmentRepository enrollmentRepository;
        private final QuizAttemptRepository quizAttemptRepository;
        private final CatalogClient catalogClient;

        public ProgressService(LessonProgressRepository lessonProgressRepository,
                        EnrollmentRepository enrollmentRepository,
                        QuizAttemptRepository quizAttemptRepository,
                        CatalogClient catalogClient) {
                this.lessonProgressRepository = lessonProgressRepository;
                this.enrollmentRepository = enrollmentRepository;
                this.quizAttemptRepository = quizAttemptRepository;
                this.catalogClient = catalogClient;
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

                        // Recalculate enrollment progress
                        updateEnrollmentProgress(enrollment);
                }

                return ProgressResponse.builder()
                                .id(progress.getId())
                                .enrollmentId(enrollment.getId())
                                .lessonId(progress.getLessonId())
                                .completed(progress.isCompleted())
                                .completedAt(progress.getCompletedAt())
                                .build();
        }

        private void updateEnrollmentProgress(Enrollment enrollment) {
                // Count completed lessons for this enrollment
                int completedLessons = lessonProgressRepository.countByEnrollmentIdAndCompletedTrue(enrollment.getId());

                // Get total lesson count from catalog service
                int totalLessons;
                try {
                        totalLessons = catalogClient.getTotalLessonCount(enrollment.getCourseId());
                } catch (FeignException e) {
                        // If we can't get the count, don't update progress
                        return;
                }

                if (totalLessons > 0) {
                        double progressPercentage = (completedLessons * 100.0) / totalLessons;
                        enrollment.setProgress(progressPercentage);
                        enrollment.setLastAccessedAt(LocalDateTime.now());

                        // Auto-complete if 100%
                        if (progressPercentage >= 100.0 && enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
                                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                                enrollment.setCompletedAt(LocalDateTime.now());
                        }

                        enrollmentRepository.save(enrollment);
                }
        }

        @Transactional
        public QuizResult submitQuiz(QuizSubmission submission) {
                Enrollment enrollment = enrollmentRepository.findById(submission.getEnrollmentId())
                                .orElseThrow(() -> new EnrollmentNotFoundException(submission.getEnrollmentId()));

                // Fetch quiz with correct answers from Catalog Service
                QuizDetailResponse quiz;
                try {
                        quiz = catalogClient.getQuizById(submission.getQuizId());
                } catch (FeignException.NotFound e) {
                        throw new ServiceCommunicationException("Quiz not found with ID: " + submission.getQuizId());
                }

                // Calculate actual score
                int correctCount = 0;
                int totalQuestions = quiz.getQuestions().size();
                Map<Long, Long> submittedAnswers = submission.getAnswers();

                for (QuestionDetailResponse question : quiz.getQuestions()) {
                        Long submittedAnswerId = submittedAnswers.get(question.getId());
                        if (submittedAnswerId != null) {
                                // Find the correct answer for this question
                                boolean isCorrect = question.getAnswers().stream()
                                                .filter(a -> a.getId().equals(submittedAnswerId))
                                                .findFirst()
                                                .map(AnswerDetailResponse::isCorrect)
                                                .orElse(false);

                                if (isCorrect) {
                                        correctCount++;
                                }
                        }
                }

                // Calculate percentage score
                int score = totalQuestions > 0 ? (correctCount * 100) / totalQuestions : 0;

                // Determine if passed based on quiz passing score (default 60% if not set)
                int passingScore = quiz.getPassingScore() != null ? quiz.getPassingScore() : 60;
                boolean passed = score >= passingScore;

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
