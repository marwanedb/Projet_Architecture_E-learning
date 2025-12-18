package e_learning.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PROFESSOR-SERVICE", path = "/professors")
public interface ProfessorServiceClient {

    @PostMapping
    ResponseEntity<ProfileResponse> createProfessor(@RequestBody ProfessorProfileRequest request);
}
