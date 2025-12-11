package e_learning.learning_service.repositories;

import e_learning.learning_service.entities.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByEnrollmentId(Long enrollmentId);

    List<QuizAttempt> findByEnrollmentIdAndQuizId(Long enrollmentId, Long quizId);
}
