package e_learning.admin_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import e_learning.admin_service.dto.CreateNotificationRequest;
import e_learning.admin_service.dto.NotificationResponse;
import e_learning.admin_service.entities.NotificationType;
import e_learning.admin_service.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        notificationResponse = NotificationResponse.builder()
                .id(1L)
                .userId(1L)
                .type(NotificationType.ENROLLMENT)
                .title("Enrollment Confirmed")
                .message("You have been enrolled in Java Programming")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /notifications - should create notification")
    void createNotification_ShouldReturnCreatedNotification() throws Exception {
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .userId(1L)
                .type(NotificationType.ENROLLMENT)
                .title("Enrollment Confirmed")
                .message("You have been enrolled in Java Programming")
                .build();

        when(notificationService.createNotification(any(CreateNotificationRequest.class)))
                .thenReturn(notificationResponse);

        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("ENROLLMENT"))
                .andExpect(jsonPath("$.title").value("Enrollment Confirmed"));

        verify(notificationService, times(1)).createNotification(any(CreateNotificationRequest.class));
    }

    @Test
    @DisplayName("GET /notifications/user/{userId} - should get user notifications")
    void getUserNotifications_ShouldReturnNotifications() throws Exception {
        List<NotificationResponse> notifications = Arrays.asList(notificationResponse);
        Page<NotificationResponse> page = new PageImpl<>(notifications);

        when(notificationService.getUserNotifications(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].userId").value(1));

        verify(notificationService, times(1)).getUserNotifications(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /notifications/user/{userId}/unread - should get unread notifications")
    void getUnreadNotifications_ShouldReturnUnreadNotifications() throws Exception {
        List<NotificationResponse> notifications = Arrays.asList(notificationResponse);

        when(notificationService.getUnreadNotifications(1L)).thenReturn(notifications);

        mockMvc.perform(get("/notifications/user/1/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].read").value(false));

        verify(notificationService, times(1)).getUnreadNotifications(1L);
    }

    @Test
    @DisplayName("GET /notifications/user/{userId}/unread-count - should get unread count")
    void getUnreadCount_ShouldReturnCount() throws Exception {
        when(notificationService.getUnreadCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/notifications/user/1/unread-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(notificationService, times(1)).getUnreadCount(1L);
    }

    @Test
    @DisplayName("POST /notifications/{id}/read - should mark notification as read")
    void markAsRead_ShouldReturnUpdatedNotification() throws Exception {
        NotificationResponse readNotification = NotificationResponse.builder()
                .id(1L)
                .read(true)
                .readAt(LocalDateTime.now())
                .build();

        when(notificationService.markAsRead(1L)).thenReturn(readNotification);

        mockMvc.perform(post("/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));

        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    @DisplayName("POST /notifications/user/{userId}/read-all - should mark all as read")
    void markAllAsRead_ShouldReturnNoContent() throws Exception {
        doNothing().when(notificationService).markAllAsRead(1L);

        mockMvc.perform(post("/notifications/user/1/read-all"))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).markAllAsRead(1L);
    }
}
