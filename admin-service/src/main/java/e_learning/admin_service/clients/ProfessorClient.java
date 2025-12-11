package e_learning.admin_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "professor-service")
public interface ProfessorClient {
    @GetMapping("/professors/all")
    List<Object> getAllProfessors();
}
