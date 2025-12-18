package e_learning.student_service.repositories;

import e_learning.student_service.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByStudentId(Long studentId);

    Optional<Wishlist> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    void deleteByStudentIdAndCourseId(Long studentId, Long courseId);
}
