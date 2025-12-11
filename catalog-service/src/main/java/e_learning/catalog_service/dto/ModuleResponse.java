package e_learning.catalog_service.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponse {
    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    private List<LessonResponse> lessons;
    private List<QuizSummaryResponse> quizzes;
}
