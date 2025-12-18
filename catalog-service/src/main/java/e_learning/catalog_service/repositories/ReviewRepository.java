package e_learning.catalog_service.repositories;

import e_learning.catalog_service.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByCourseId(Long courseId, Pageable pageable);

    List<Review> findByStudentId(Long studentId);

    Optional<Review> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
    Double calculateAverageRating(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
}
