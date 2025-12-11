package e_learning.learning_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgressRequest {
    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;

    @NotNull(message = "Lesson ID is required")
    private Long lessonId;
}
