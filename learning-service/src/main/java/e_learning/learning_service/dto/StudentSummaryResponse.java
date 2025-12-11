package e_learning.learning_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSummaryResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
