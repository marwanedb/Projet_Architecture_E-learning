package e_learning.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "enrollment_id", "lessonId" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(nullable = false)
    private Long lessonId; // Reference to catalog-service Lesson ID

    @Builder.Default
    private boolean completed = false;

    private LocalDateTime completedAt;
}
