package e_learning.student_service.repositories;

import e_learning.student_service.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);

    Optional<Student> findByAuthId(Long authId);

    Optional<Student> findByCne(String cne);

    boolean existsByEmail(String email);

    boolean existsByAuthId(Long authId);

    boolean existsByCne(String cne);
}
