package e_learning.learning_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressResponse {
    private Long id;
    private Long enrollmentId;
    private Long lessonId;
    private boolean completed;
    private LocalDateTime completedAt;
}
