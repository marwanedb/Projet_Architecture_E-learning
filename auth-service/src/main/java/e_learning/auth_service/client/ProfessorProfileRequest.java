package e_learning.auth_service.client;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessorProfileRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String specialization;
    private String bio;
    private String phoneNumber;
    private String address;
    private String profilePictureUrl;
    private LocalDateTime dateOfBirth;
    private Long authId;
}
