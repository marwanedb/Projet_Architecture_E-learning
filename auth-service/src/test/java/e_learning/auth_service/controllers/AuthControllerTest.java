package e_learning.auth_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import e_learning.auth_service.dto.*;
import e_learning.auth_service.entities.Role;
import e_learning.auth_service.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthResponse authResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        authResponse = AuthResponse.builder()
                .userId(1L)
                .email("test@example.com")
                .role(Role.STUDENT)
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .expiresIn(3600L)
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.STUDENT)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /auth/register - should register new user")
    void register_ShouldReturnAuthResponse() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole(Role.STUDENT);

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.accessToken").exists());

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /auth/login - should login user")
    void login_ShouldReturnAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /auth/refresh - should refresh token")
    void refreshToken_ShouldReturnNewTokens() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-refresh-token");

        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());

        verify(authService, times(1)).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("POST /auth/logout/{userId} - should logout user")
    void logout_ShouldReturnNoContent() throws Exception {
        doNothing().when(authService).logout(1L);

        mockMvc.perform(post("/auth/logout/1"))
                .andExpect(status().isNoContent());

        verify(authService, times(1)).logout(1L);
    }

    @Test
    @DisplayName("GET /auth/users/{id} - should get user by ID")
    void getUserById_ShouldReturnUser() throws Exception {
        when(authService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/auth/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("PUT /auth/users/{id}/lock - should lock user")
    void lockUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(authService).lockUser(1L, true);

        mockMvc.perform(put("/auth/users/1/lock")
                .param("locked", "true"))
                .andExpect(status().isNoContent());

        verify(authService, times(1)).lockUser(1L, true);
    }

    @Test
    @DisplayName("POST /auth/forgot-password - should return reset token")
    void forgotPassword_ShouldReturnToken() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");

        when(authService.forgotPassword(any(ForgotPasswordRequest.class))).thenReturn("test-reset-token");

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("test-reset-token")));

        verify(authService, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    @DisplayName("POST /auth/reset-password - should reset password")
    void resetPassword_ShouldReturnSuccess() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("test-reset-token");
        request.setNewPassword("newpassword123");

        doNothing().when(authService).resetPassword(any(ResetPasswordRequest.class));

        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("successful")));

        verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
    }
}
