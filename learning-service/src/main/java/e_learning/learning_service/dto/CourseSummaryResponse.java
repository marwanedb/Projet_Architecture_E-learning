package e_learning.learning_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummaryResponse {
    private Long id;
    private String title;
    private String status;
}
