package e_learning.student_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentRequest {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 20, message = "CNE cannot exceed 20 characters")
    private String cne;

    private String phoneNumber;

    private String address;

    private String profilePictureUrl;

    private LocalDateTime dateOfBirth;

    // Required to link with auth
    @NotNull(message = "Auth ID is required")
    private Long authId;
}
