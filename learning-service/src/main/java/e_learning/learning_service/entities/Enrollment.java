package e_learning.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long courseId;

    private LocalDate enrollmentDate;

    // On ne stocke pas tout le cours, juste son ID.
    // Mais on peut avoir un champ non-persistant pour l'affichage (Transient)
    @Transient
    private Object courseDetails;
}
