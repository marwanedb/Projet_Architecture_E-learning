package e_learning.learning_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResult {
    private Long attemptId;
    private Long quizId;
    private int score;
    private int totalQuestions;
    private boolean passed;
    private LocalDateTime attemptedAt;
}
