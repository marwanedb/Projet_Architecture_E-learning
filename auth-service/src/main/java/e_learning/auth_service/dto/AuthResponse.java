package e_learning.auth_service.dto;

import e_learning.auth_service.entities.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long userId;
    private String email;
    private Role role;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}
