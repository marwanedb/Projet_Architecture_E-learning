package e_learning.learning_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import e_learning.learning_service.dto.*;
import e_learning.learning_service.services.ProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgressController.class)
class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProgressService progressService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProgressResponse progressResponse;
    private QuizResult quizResult;

    @BeforeEach
    void setUp() {
        progressResponse = ProgressResponse.builder()
                .id(1L)
                .enrollmentId(1L)
                .lessonId(1L)
                .completed(true)
                .completedAt(LocalDateTime.now())
                .build();

        quizResult = QuizResult.builder()
                .attemptId(1L)
                .quizId(1L)
                .score(85)
                .totalQuestions(10)
                .passed(true)
                .attemptedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /progress/lessons - should mark lesson as complete")
    void markLessonComplete_ShouldReturnProgress() throws Exception {
        LessonProgressRequest request = new LessonProgressRequest();
        request.setEnrollmentId(1L);
        request.setLessonId(1L);

        when(progressService.markLessonAsComplete(any(LessonProgressRequest.class))).thenReturn(progressResponse);

        mockMvc.perform(post("/progress/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.lessonId").value(1));

        verify(progressService, times(1)).markLessonAsComplete(any(LessonProgressRequest.class));
    }

    @Test
    @DisplayName("POST /progress/quizzes - should submit quiz with real scoring")
    void submitQuiz_ShouldReturnQuizResult() throws Exception {
        Map<Long, Long> answers = new HashMap<>();
        answers.put(1L, 1L);
        answers.put(2L, 5L);

        QuizSubmission submission = new QuizSubmission();
        submission.setEnrollmentId(1L);
        submission.setQuizId(1L);
        submission.setAnswers(answers);

        when(progressService.submitQuiz(any(QuizSubmission.class))).thenReturn(quizResult);

        mockMvc.perform(post("/progress/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(85))
                .andExpect(jsonPath("$.passed").value(true))
                .andExpect(jsonPath("$.totalQuestions").value(10));

        verify(progressService, times(1)).submitQuiz(any(QuizSubmission.class));
    }

    @Test
    @DisplayName("POST /progress/quizzes - should handle failing quiz")
    void submitQuiz_WhenFailed_ShouldReturnFailedResult() throws Exception {
        QuizResult failedResult = QuizResult.builder()
                .attemptId(1L)
                .quizId(1L)
                .score(40)
                .totalQuestions(10)
                .passed(false)
                .attemptedAt(LocalDateTime.now())
                .build();

        Map<Long, Long> answers = new HashMap<>();
        answers.put(1L, 2L);

        QuizSubmission submission = new QuizSubmission();
        submission.setEnrollmentId(1L);
        submission.setQuizId(1L);
        submission.setAnswers(answers);

        when(progressService.submitQuiz(any(QuizSubmission.class))).thenReturn(failedResult);

        mockMvc.perform(post("/progress/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passed").value(false))
                .andExpect(jsonPath("$.score").value(40));

        verify(progressService, times(1)).submitQuiz(any(QuizSubmission.class));
    }
}
