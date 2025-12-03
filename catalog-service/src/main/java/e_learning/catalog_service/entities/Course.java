package e_learning.catalog_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String level;
    private Long professorId;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Module> modules;
}