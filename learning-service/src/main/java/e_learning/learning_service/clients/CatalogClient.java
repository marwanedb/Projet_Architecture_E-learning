package e_learning.learning_service.clients;

import e_learning.learning_service.dto.CourseSummaryResponse;
import e_learning.learning_service.dto.QuizDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", contextId = "CatalogClient")
public interface CatalogClient {
    @GetMapping("/courses/{id}")
    CourseSummaryResponse getCourseById(@PathVariable("id") Long id);

    @GetMapping("/content/quizzes/{quizId}")
    QuizDetailResponse getQuizById(@PathVariable("quizId") Long quizId);

    @GetMapping("/courses/{id}/lesson-count")
    Integer getTotalLessonCount(@PathVariable("id") Long courseId);
}
