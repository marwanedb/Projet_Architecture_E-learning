package e_learning.auth_service.dto;

import e_learning.auth_service.entities.Role;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FullRegistrationRequest {
    // Auth fields
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    // Profile fields (common to both student and professor)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    private String phoneNumber;
    private String address;
    private String profilePictureUrl;
    private LocalDateTime dateOfBirth;

    // Student-specific fields
    private String cne;

    // Professor-specific fields
    private String department;
    private String specialization;
    private String bio;
}
