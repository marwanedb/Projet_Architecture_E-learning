package e_learning.catalog_service.repositories;

import e_learning.catalog_service.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId);

    List<Lesson> findByModuleId(Long moduleId);
}
