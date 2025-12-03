package e_learning.learning_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name = nom du service dans Eureka (INSENSIBLE À LA CASSE ICI)
@FeignClient(name = "catalog-service")
public interface CourseRestClient {

    // On définit juste la signature de la méthode qu'on veut appeler
    @GetMapping("/courses/{id}")
    Object getCourseById(@PathVariable("id") Long id);
    // Note : J'ai mis "Object" pour faire simple, on pourrait créer une classe CourseDTO
}
