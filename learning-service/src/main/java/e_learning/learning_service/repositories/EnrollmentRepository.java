package e_learning.learning_service.repositories;


import e_learning.learning_service.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // Spring Data JPA gère tout automatiquement (save, findById, etc.)
}
