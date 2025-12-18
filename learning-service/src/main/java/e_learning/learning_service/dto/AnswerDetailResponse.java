package e_learning.learning_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDetailResponse {
    private Long id;
    private String answerText;
    private boolean correct;
}
