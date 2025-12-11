package e_learning.auth_service.dto;

import e_learning.auth_service.entities.Role;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
