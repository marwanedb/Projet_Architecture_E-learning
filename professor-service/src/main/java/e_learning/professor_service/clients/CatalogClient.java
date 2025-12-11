package e_learning.professor_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "catalog-service")
public interface CatalogClient {
    @GetMapping("/courses/professor/{professorId}")
    List<Object> getCoursesByProfessor(@PathVariable("professorId") Long professorId);
}
