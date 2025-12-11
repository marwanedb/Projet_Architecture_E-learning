package e_learning.admin_service.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long adminId;

    @Column(nullable = false)
    private String action;

    private String targetType; // e.g., "USER", "COURSE"

    private String targetId;

    private String details;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
