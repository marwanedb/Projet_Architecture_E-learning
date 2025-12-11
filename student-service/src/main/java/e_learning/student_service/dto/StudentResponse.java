package e_learning.student_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String cne;
    private String phoneNumber;
    private String address;
    private String profilePictureUrl;
    private LocalDateTime dateOfBirth;
    private LocalDateTime createdAt;
    private Long authId;
}
