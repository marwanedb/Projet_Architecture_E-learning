package e_learning.professor_service.repositories;

import e_learning.professor_service.entities.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByEmail(String email);

    Optional<Professor> findByAuthId(Long authId);

    List<Professor> findByDepartment(String department);

    boolean existsByEmail(String email);

    boolean existsByAuthId(Long authId);
}
