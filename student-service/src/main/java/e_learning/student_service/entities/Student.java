package e_learning.student_service.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String cne; // Code National Étudiant (Matricule)

    private String phoneNumber;

    private String address;

    private String profilePictureUrl;

    private LocalDateTime dateOfBirth;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Link to Auth Service
    @Column(unique = true)
    private Long authId;
}
