package e_learning.catalog_service.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private LessonType type;

    private String url;

    private Integer durationMinutes;

    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "module_id")
    @JsonIgnore
    private Module module;
}
