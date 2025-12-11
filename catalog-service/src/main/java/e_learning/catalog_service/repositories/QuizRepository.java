package e_learning.catalog_service.repositories;

import e_learning.catalog_service.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByModuleId(Long moduleId);
}
