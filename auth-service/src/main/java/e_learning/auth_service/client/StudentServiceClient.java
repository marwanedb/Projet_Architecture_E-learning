package e_learning.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "STUDENT-SERVICE", path = "/students")
public interface StudentServiceClient {

    @PostMapping
    ResponseEntity<ProfileResponse> createStudent(@RequestBody StudentProfileRequest request);
}
