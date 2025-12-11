package e_learning.learning_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmission {
    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    // Map of <QuestionId, AnswerId>
    @NotEmpty(message = "Answers are required")
    private Map<Long, Long> answers;
}
