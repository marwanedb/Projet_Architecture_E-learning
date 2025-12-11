package e_learning.auth_service.services;

import e_learning.auth_service.dto.*;
import e_learning.auth_service.entities.AuthUser;
import e_learning.auth_service.exceptions.*;
import e_learning.auth_service.repositories.AuthUserRepository;
import e_learning.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Create new user
        AuthUser user = AuthUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        // Generate tokens
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        AuthUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isActive()) {
            throw new AccountDisabledException();
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        AuthUser user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expired");
        }

        return generateAuthResponse(user);
    }

    @Transactional
    public void logout(Long userId) {
        AuthUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
    }

    public UserResponse getUserById(Long id) {
        AuthUser user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapToUserResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        AuthUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return mapToUserResponse(user);
    }

    public boolean validateToken(String token) {
        return jwtService.isTokenValid(token);
    }

    private AuthResponse generateAuthResponse(AuthUser user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Store refresh token
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    private UserResponse mapToUserResponse(AuthUser user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    @Transactional
    public void lockUser(Long userId, boolean locked) {
        AuthUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setLocked(locked);
        // Also invalidate session by clearing tokens if locking
        if (locked) {
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
        }
        userRepository.save(user);
    }
}
