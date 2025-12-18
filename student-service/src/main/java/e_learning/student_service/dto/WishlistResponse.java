package e_learning.student_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponse {
    private Long id;
    private Long studentId;
    private Long courseId;
    private LocalDateTime addedAt;
}
