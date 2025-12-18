package e_learning.learning_service.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDetailResponse {
    private Long id;
    private String title;
    private String description;
    private Integer passingScore;
    private Integer timeLimitMinutes;
    private List<QuestionDetailResponse> questions;
}
