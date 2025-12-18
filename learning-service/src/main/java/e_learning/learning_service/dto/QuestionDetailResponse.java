package e_learning.learning_service.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDetailResponse {
    private Long id;
    private String questionText;
    private String type;
    private Integer points;
    private List<AnswerDetailResponse> answers;
}
