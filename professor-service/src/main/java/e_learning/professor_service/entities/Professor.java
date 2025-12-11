package e_learning.professor_service.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "professors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String department;

    private String specialization;

    private String bio;

    private String profilePictureUrl;

    private String officeLocation;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Link to Auth Service
    @Column(unique = true)
    private Long authId;
}
