package e_learning.admin_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service")
public interface AuthClient {
    @PutMapping("/auth/users/{id}/lock")
    void lockUser(@PathVariable("id") Long id, @RequestParam("locked") boolean locked);
}
