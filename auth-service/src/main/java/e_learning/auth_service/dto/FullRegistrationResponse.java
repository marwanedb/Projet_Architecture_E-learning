package e_learning.auth_service.dto;

import e_learning.auth_service.entities.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FullRegistrationResponse {
    // Auth info
    private Long userId;
    private String email;
    private Role role;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    // Profile info
    private Long profileId;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
}
