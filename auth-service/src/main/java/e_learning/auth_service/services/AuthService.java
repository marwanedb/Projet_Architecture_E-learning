package e_learning.auth_service.services;

import e_learning.auth_service.client.*;
import e_learning.auth_service.dto.*;
import e_learning.auth_service.entities.AuthUser;
import e_learning.auth_service.entities.PasswordResetToken;
import e_learning.auth_service.entities.Role;
import e_learning.auth_service.exceptions.*;
import e_learning.auth_service.repositories.AuthUserRepository;
import e_learning.auth_service.repositories.PasswordResetTokenRepository;
import e_learning.auth_service.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthUserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final StudentServiceClient studentServiceClient;
    private final ProfessorServiceClient professorServiceClient;

    public AuthService(AuthUserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            StudentServiceClient studentServiceClient,
            ProfessorServiceClient professorServiceClient) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.studentServiceClient = studentServiceClient;
        this.professorServiceClient = professorServiceClient;
    }

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

    /**
     * Full registration - creates both auth user AND profile in one call.
     * This is the recommended endpoint for frontend integration.
     */
    @Transactional
    public FullRegistrationResponse registerFull(FullRegistrationRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Create auth user first
        AuthUser user = AuthUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);
        log.info("Created auth user with ID: {}", user.getId());

        // Create profile based on role
        ProfileResponse profileResponse;
        try {
            if (request.getRole() == Role.STUDENT) {
                StudentProfileRequest studentRequest = StudentProfileRequest.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .cne(request.getCne())
                        .phoneNumber(request.getPhoneNumber())
                        .address(request.getAddress())
                        .profilePictureUrl(request.getProfilePictureUrl())
                        .dateOfBirth(request.getDateOfBirth())
                        .authId(user.getId())
                        .build();

                ResponseEntity<ProfileResponse> response = studentServiceClient.createStudent(studentRequest);
                profileResponse = response.getBody();
                log.info("Created student profile with ID: {}",
                        profileResponse != null ? profileResponse.getId() : "null");

            } else if (request.getRole() == Role.PROFESSOR) {
                ProfessorProfileRequest professorRequest = ProfessorProfileRequest.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .department(request.getDepartment())
                        .specialization(request.getSpecialization())
                        .bio(request.getBio())
                        .phoneNumber(request.getPhoneNumber())
                        .address(request.getAddress())
                        .profilePictureUrl(request.getProfilePictureUrl())
                        .dateOfBirth(request.getDateOfBirth())
                        .authId(user.getId())
                        .build();

                ResponseEntity<ProfileResponse> response = professorServiceClient.createProfessor(professorRequest);
                profileResponse = response.getBody();
                log.info("Created professor profile with ID: {}",
                        profileResponse != null ? profileResponse.getId() : "null");

            } else {
                // Admin role - no separate profile needed
                profileResponse = ProfileResponse.builder()
                        .id(user.getId())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .build();
            }
        } catch (Exception e) {
            log.error("Failed to create profile, rolling back auth user: {}", e.getMessage());
            // Delete the auth user since profile creation failed
            userRepository.delete(user);
            throw new RuntimeException("Failed to create profile: " + e.getMessage(), e);
        }

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Store refresh token
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return FullRegistrationResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpiration())
                .profileId(profileResponse != null ? profileResponse.getId() : null)
                .firstName(profileResponse != null ? profileResponse.getFirstName() : null)
                .lastName(profileResponse != null ? profileResponse.getLastName() : null)
                .profilePictureUrl(profileResponse != null ? profileResponse.getProfilePictureUrl() : null)
                .build();
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

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        AuthUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByUserId(user.getId());

        // Generate new reset token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // In a real application, you would send an email with the reset link
        // For this academic project, we just return the token
        return token;
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (resetToken.isUsed()) {
            throw new InvalidTokenException("Reset token has already been used");
        }

        if (resetToken.isExpired()) {
            throw new InvalidTokenException("Reset token has expired");
        }

        // Update password
        AuthUser user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Invalidate any existing sessions
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
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
