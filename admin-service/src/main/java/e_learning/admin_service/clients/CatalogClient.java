package e_learning.admin_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "catalog-service")
public interface CatalogClient {
    @GetMapping("/courses")
    Object getAllCourses(); // The endpoint in catalog service returns Page by default, might need
                            // adjustment.
    // Actually, looking back at CatalogController, GET /courses is paginated,
    // returning Page<CourseResponse>.
    // To get total elements, we can use the page metadata.
    // Or add a count endpoint in Catalog Service.
    // For simplicity here, I'll access the paginated response and extract
    // totalElements if the DTO structure allows,
    // OR just use a simpler approach of adding /courses/all endpoint in Catalog
    // Service later?
    // Wait, Catalog Service DOES NOT have /courses/all (no pagination).
    // I should add a count endpoint to other services for efficiency, but that
    // breaks the flow of just working on Admin Service.
    // I'll stick to calling the paginated endpoint with size=1 just to get the
    // totalElements from metadata?
    // Or I can add a specific count endpoint if I modify other services.
    // Let's assume for now I'll just map the response to a custom Page structure to
    // get totalElements.
}
