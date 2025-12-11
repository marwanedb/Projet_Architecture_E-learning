package e_learning.learning_service.clients;

import e_learning.learning_service.dto.StudentSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student-service")
public interface StudentClient {
    @GetMapping("/students/{id}")
    StudentSummaryResponse getStudentById(@PathVariable("id") Long id);
}
