package e_learning.catalog_service.dto;

import e_learning.catalog_service.entities.CourseLevel;
import e_learning.catalog_service.entities.CourseStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private CourseLevel level;
    private CourseStatus status;
    private Long professorId;
    private String thumbnailUrl;
    private Double price;
    private Integer durationHours;
    private Double averageRating;
    private Integer totalEnrollments;
    private String categoryName;
    private Integer moduleCount;
}
