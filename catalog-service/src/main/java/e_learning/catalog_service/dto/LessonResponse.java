package e_learning.catalog_service.dto;

import e_learning.catalog_service.entities.LessonType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {
    private Long id;
    private String title;
    private String description;
    private LessonType type;
    private String url;
    private Integer durationMinutes;
    private Integer orderIndex;
}
