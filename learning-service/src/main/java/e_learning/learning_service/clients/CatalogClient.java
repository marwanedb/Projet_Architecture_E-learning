package e_learning.learning_service.clients;

import e_learning.learning_service.dto.CourseSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service",contextId = "CatalogClient")
public interface CatalogClient {
    @GetMapping("/courses/{id}")
    CourseSummaryResponse getCourseById(@PathVariable("id") Long id);
}
