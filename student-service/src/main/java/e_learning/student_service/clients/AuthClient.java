package e_learning.student_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthClient {
    // We need a way to check if user exists, and optionally check role.
    // AuthController has /auth/users/{id} which returns UserResponse.
    @GetMapping("/auth/users/{id}")
    Object getUserById(@PathVariable("id") Long id); // Returning Object to avoid DTO duplication for now, or strict
                                                     // checking.
    // Ideally we should have a simple DTO representation.
}
