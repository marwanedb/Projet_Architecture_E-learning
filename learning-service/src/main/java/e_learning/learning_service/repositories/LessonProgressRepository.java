package e_learning.learning_service.repositories;

import e_learning.learning_service.entities.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    List<LessonProgress> findByEnrollmentId(Long enrollmentId);

    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);

    int countByEnrollmentIdAndCompletedTrue(Long enrollmentId);
}
