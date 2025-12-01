package e_learning.auth_service.controllers;


import e_learning.auth_service.entities.AuthUser;
import e_learning.auth_service.repositories.AuthUserRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthUserRepository authUserRepository;

    public AuthController(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @PostMapping("/register")
    public AuthUser register(@RequestBody AuthUser user) {
        // Validation basique (à améliorer plus tard)
        if(authUserRepository.findByEmail(user  .getEmail()).isPresent()) {
            throw new RuntimeException("Cet email existe déjà !");
        }

        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        // On ne crypte pas encore le mot de passe pour ce test simple
        return authUserRepository.save(user);
    }
}
