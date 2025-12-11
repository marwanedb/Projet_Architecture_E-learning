package e_learning.learning_service.dto;

import e_learning.learning_service.entities.EnrollmentStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String courseTitle; // Enriched data
    private String studentName; // Enriched data
    private EnrollmentStatus status;
    private Double progress;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
}
