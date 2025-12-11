package e_learning.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "studentId", "courseId" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    @Builder.Default
    private Double progress = 0.0;

    @Builder.Default
    private LocalDateTime enrolledAt = LocalDateTime.now();

    private LocalDateTime completedAt;

    private LocalDateTime lastAccessedAt;
}
