package e_learning.gateway_service.filter;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteValidator {

    // Endpoints that don't require authentication
    private static final List<String> OPEN_ENDPOINTS = List.of(
            // Auth endpoints
            "/auth-service/auth/register",
            "/auth-service/auth/register-full",
            "/auth-service/auth/login",
            "/auth-service/auth/refresh",
            "/auth-service/auth/validate",
            "/auth-service/auth/forgot-password",
            "/auth-service/auth/reset-password",

            // Swagger/OpenAPI endpoints
            "/auth-service/swagger-ui",
            "/auth-service/v3/api-docs",
            "/catalog-service/swagger-ui",
            "/catalog-service/v3/api-docs",
            "/student-service/swagger-ui",
            "/student-service/v3/api-docs",
            "/professor-service/swagger-ui",
            "/professor-service/v3/api-docs",
            "/learning-service/swagger-ui",
            "/learning-service/v3/api-docs",
            "/admin-service/swagger-ui",
            "/admin-service/v3/api-docs",

            // Public catalog endpoints - courses can be viewed without auth
            "/catalog-service/courses",
            "/catalog-service/courses/published",
            "/catalog-service/courses/search",

            // Public categories - needed for filter dropdowns
            "/catalog-service/categories",

            // Public reviews - users can read reviews before enrolling
            "/catalog-service/reviews/course",

            // Public certificate verification
            "/learning-service/certificates/verify",

            // Public static file access for course media
            "/catalog-service/uploads");

    public boolean isOpenEndpoint(String path) {
        return OPEN_ENDPOINTS.stream()
                .anyMatch(endpoint -> path.startsWith(endpoint) || path.equals(endpoint));
    }
}
