package e_learning.auth_service.client;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePictureUrl;
    private LocalDateTime createdAt;
}
