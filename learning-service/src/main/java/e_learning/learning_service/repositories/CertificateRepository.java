package e_learning.learning_service.repositories;

import e_learning.learning_service.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByCertificateCode(String certificateCode);

    Optional<Certificate> findByEnrollmentId(Long enrollmentId);

    List<Certificate> findByEnrollmentStudentId(Long studentId);

    boolean existsByEnrollmentId(Long enrollmentId);
}
