package e_learning.catalog_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private Integer passingScore;
    private Integer timeLimitMinutes;
    private Integer questionCount;
}
