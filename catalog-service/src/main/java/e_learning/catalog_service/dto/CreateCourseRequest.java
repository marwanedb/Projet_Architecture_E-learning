package e_learning.catalog_service.dto;

import e_learning.catalog_service.entities.CourseLevel;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCourseRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private CourseLevel level;

    @NotNull(message = "Professor ID is required")
    private Long professorId;

    private Long categoryId;

    private String thumbnailUrl;

    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;

    @Positive(message = "Duration must be positive")
    private Integer durationHours;
}
