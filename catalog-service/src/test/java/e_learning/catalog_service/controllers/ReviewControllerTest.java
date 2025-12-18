package e_learning.catalog_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import e_learning.catalog_service.dto.ReviewRequest;
import e_learning.catalog_service.dto.ReviewResponse;
import e_learning.catalog_service.services.ReviewService;
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

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {
        reviewResponse = ReviewResponse.builder()
                .id(1L)
                .studentId(1L)
                .courseId(1L)
                .courseTitle("Java Programming")
                .rating(5)
                .comment("Excellent course!")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /reviews - should create new review")
    void createReview_ShouldReturnCreatedReview() throws Exception {
        ReviewRequest request = ReviewRequest.builder()
                .studentId(1L)
                .courseId(1L)
                .rating(5)
                .comment("Excellent course!")
                .build();

        when(reviewService.createReview(any(ReviewRequest.class))).thenReturn(reviewResponse);

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excellent course!"));

        verify(reviewService, times(1)).createReview(any(ReviewRequest.class));
    }

    @Test
    @DisplayName("PUT /reviews/{id} - should update review")
    void updateReview_ShouldReturnUpdatedReview() throws Exception {
        ReviewRequest request = ReviewRequest.builder()
                .studentId(1L)
                .courseId(1L)
                .rating(4)
                .comment("Good course!")
                .build();

        ReviewResponse updated = ReviewResponse.builder()
                .id(1L)
                .rating(4)
                .comment("Good course!")
                .build();

        when(reviewService.updateReview(eq(1L), any(ReviewRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4));

        verify(reviewService, times(1)).updateReview(eq(1L), any(ReviewRequest.class));
    }

    @Test
    @DisplayName("DELETE /reviews/{id} - should delete review")
    void deleteReview_ShouldReturnNoContent() throws Exception {
        doNothing().when(reviewService).deleteReview(1L, 1L);

        mockMvc.perform(delete("/reviews/1")
                .param("studentId", "1"))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).deleteReview(1L, 1L);
    }

    @Test
    @DisplayName("GET /reviews/{id} - should get review by ID")
    void getReviewById_ShouldReturnReview() throws Exception {
        when(reviewService.getReviewById(1L)).thenReturn(reviewResponse);

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5));

        verify(reviewService, times(1)).getReviewById(1L);
    }

    @Test
    @DisplayName("GET /reviews/course/{courseId} - should get course reviews")
    void getCourseReviews_ShouldReturnReviews() throws Exception {
        List<ReviewResponse> reviews = Arrays.asList(reviewResponse);
        Page<ReviewResponse> page = new PageImpl<>(reviews);

        when(reviewService.getCourseReviews(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/reviews/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].courseId").value(1));

        verify(reviewService, times(1)).getCourseReviews(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /reviews/student/{studentId} - should get student reviews")
    void getStudentReviews_ShouldReturnReviews() throws Exception {
        List<ReviewResponse> reviews = Arrays.asList(reviewResponse);

        when(reviewService.getStudentReviews(1L)).thenReturn(reviews);

        mockMvc.perform(get("/reviews/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].studentId").value(1));

        verify(reviewService, times(1)).getStudentReviews(1L);
    }
}
