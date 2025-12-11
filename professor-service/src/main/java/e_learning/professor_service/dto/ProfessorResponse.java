package e_learning.professor_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String specialization;
    private String bio;
    private String profilePictureUrl;
    private String officeLocation;
    private LocalDateTime createdAt;
    private Long authId;
}
