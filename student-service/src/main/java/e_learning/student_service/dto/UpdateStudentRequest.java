package e_learning.student_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentRequest {
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(max = 20, message = "CNE cannot exceed 20 characters")
    private String cne;

    private String phoneNumber;

    private String address;

    private String profilePictureUrl;

    private LocalDateTime dateOfBirth;
}
