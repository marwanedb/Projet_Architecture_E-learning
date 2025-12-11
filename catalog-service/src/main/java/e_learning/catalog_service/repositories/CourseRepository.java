package e_learning.catalog_service.repositories;

import e_learning.catalog_service.entities.Course;
import e_learning.catalog_service.entities.CourseLevel;
import e_learning.catalog_service.entities.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find by status
    List<Course> findByStatus(CourseStatus status);

    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    // Find by category
    List<Course> findByCategoryId(Long categoryId);

    Page<Course> findByCategoryId(Long categoryId, Pageable pageable);

    // Find by professor
    List<Course> findByProfessorId(Long professorId);

    // Find by level
    List<Course> findByLevel(CourseLevel level);

    // Search by title (case insensitive)
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Course> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    // Advanced search
    @Query("SELECT c FROM Course c WHERE " +
            "(:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR c.category.id = :categoryId) AND " +
            "(:level IS NULL OR c.level = :level) AND " +
            "(:status IS NULL OR c.status = :status)")
    Page<Course> searchCourses(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("level") CourseLevel level,
            @Param("status") CourseStatus status,
            Pageable pageable);

    // Find published courses only
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED'")
    Page<Course> findAllPublished(Pageable pageable);

    // Count by category
    Long countByCategoryId(Long categoryId);

    // Count by professor
    Long countByProfessorId(Long professorId);
}