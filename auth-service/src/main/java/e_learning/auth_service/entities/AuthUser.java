package e_learning.auth_service.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean active;

    @Builder.Default
    private boolean locked = false;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    @Column(length = 500)
    private String refreshToken;

    private LocalDateTime refreshTokenExpiry;
}
