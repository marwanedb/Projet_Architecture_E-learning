package e_learning.auth_service.repositories;

import e_learning.auth_service.entities.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByEmail(String email);

    Optional<AuthUser> findByRefreshToken(String refreshToken);

    boolean existsByEmail(String email);
}
