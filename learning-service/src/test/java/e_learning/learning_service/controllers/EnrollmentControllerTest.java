package e_learning.learning_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import e_learning.learning_service.dto.*;
import e_learning.learning_service.entities.EnrollmentStatus;
import e_learning.learning_service.services.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentService enrollmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private EnrollmentResponse enrollmentResponse;

    @BeforeEach
    void setUp() {
        enrollmentResponse = EnrollmentResponse.builder()
                .id(1L)
                .studentId(1L)
                .courseId(1L)
                .status(EnrollmentStatus.ACTIVE)
                .progress(0.0)
                .enrolledAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /enrollments - should create new enrollment")
    void createEnrollment_ShouldReturnCreatedEnrollment() throws Exception {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setStudentId(1L);
        request.setCourseId(1L);

        when(enrollmentService.createEnrollment(any(EnrollmentRequest.class))).thenReturn(enrollmentResponse);

        mockMvc.perform(post("/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.courseId").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(enrollmentService, times(1)).createEnrollment(any(EnrollmentRequest.class));
    }

    @Test
    @DisplayName("GET /enrollments/{id} - should get enrollment by ID")
    void getEnrollmentById_ShouldReturnEnrollment() throws Exception {
        when(enrollmentService.getEnrollmentById(1L)).thenReturn(enrollmentResponse);

        mockMvc.perform(get("/enrollments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(enrollmentService, times(1)).getEnrollmentById(1L);
    }

    @Test
    @DisplayName("GET /enrollments/student/{studentId} - should get student enrollments")
    void getStudentEnrollments_ShouldReturnEnrollments() throws Exception {
        List<EnrollmentResponse> enrollments = Arrays.asList(enrollmentResponse);

        when(enrollmentService.getStudentEnrollments(1L)).thenReturn(enrollments);

        mockMvc.perform(get("/enrollments/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].studentId").value(1));

        verify(enrollmentService, times(1)).getStudentEnrollments(1L);
    }

    @Test
    @DisplayName("GET /enrollments/course/{courseId} - should get course enrollments")
    void getCourseEnrollments_ShouldReturnEnrollments() throws Exception {
        List<EnrollmentResponse> enrollments = Arrays.asList(enrollmentResponse);

        when(enrollmentService.getCourseEnrollments(1L)).thenReturn(enrollments);

        mockMvc.perform(get("/enrollments/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].courseId").value(1));

        verify(enrollmentService, times(1)).getCourseEnrollments(1L);
    }

    @Test
    @DisplayName("DELETE /enrollments/{id} - should cancel enrollment")
    void cancelEnrollment_ShouldReturnNoContent() throws Exception {
        doNothing().when(enrollmentService).cancelEnrollment(1L);

        mockMvc.perform(delete("/enrollments/1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService, times(1)).cancelEnrollment(1L);
    }

    @Test
    @DisplayName("POST /enrollments/{id}/drop - should drop course")
    void dropCourse_ShouldReturnEnrollment() throws Exception {
        EnrollmentResponse dropped = EnrollmentResponse.builder()
                .id(1L)
                .status(EnrollmentStatus.DROPPED)
                .build();

        when(enrollmentService.dropCourse(1L)).thenReturn(dropped);

        mockMvc.perform(post("/enrollments/1/drop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DROPPED"));

        verify(enrollmentService, times(1)).dropCourse(1L);
    }

    @Test
    @DisplayName("GET /enrollments/check - should check if enrolled")
    void isEnrolled_ShouldReturnBoolean() throws Exception {
        when(enrollmentService.isEnrolled(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/enrollments/check")
                .param("studentId", "1")
                .param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(enrollmentService, times(1)).isEnrolled(1L, 1L);
    }
}
