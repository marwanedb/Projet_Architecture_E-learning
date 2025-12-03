package e_learning.catalog_service.repositories;

import e_learning.catalog_service.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}