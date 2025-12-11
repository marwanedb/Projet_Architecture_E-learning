package e_learning.admin_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "student-service")
public interface StudentClient {
    @GetMapping("/students/all")
    List<Object> getAllStudents(); // We just need the count, finding list size is easier for now than adding count
                                   // endpoint everywhere
}
