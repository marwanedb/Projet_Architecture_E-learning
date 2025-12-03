package e_learning.catalog_service.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // Type de contenu : "VIDEO", "PDF", "QUIZ"
    private String type;

    // L'URL (Youtube ou lien fichier)
    private String url;

    @ManyToOne
    @JoinColumn(name = "module_id")
    @JsonIgnore // <--- TRÈS IMPORTANT : Évite la boucle infinie en JSON
    private Module module;
}
