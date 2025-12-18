package e_learning.catalog_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import e_learning.catalog_service.dto.*;
import e_learning.catalog_service.entities.CourseLevel;
import e_learning.catalog_service.entities.CourseStatus;
import e_learning.catalog_service.services.CourseService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    private CourseResponse courseResponse;
    private CourseSummaryResponse courseSummary;

    @BeforeEach
    void setUp() {
        courseResponse = CourseResponse.builder()
                .id(1L)
                .title("Java Programming")
                .description("Learn Java from scratch")
                .level(CourseLevel.BEGINNER)
                .status(CourseStatus.DRAFT)
                .professorId(1L)
                .price(new BigDecimal("49.99"))
                .durationHours(20)
                .averageRating(4.5)
                .totalEnrollments(100)
                .createdAt(LocalDateTime.now())
                .build();

        courseSummary = CourseSummaryResponse.builder()
                .id(1L)
                .title("Java Programming")
                .description("Learn Java from scratch")
                .level(CourseLevel.BEGINNER)
                .status(CourseStatus.PUBLISHED)
                .professorId(1L)
                .price(new BigDecimal("49.99"))
                .averageRating(4.5)
                .build();
    }

    @Test
    @DisplayName("POST /courses - should create new course")
    void createCourse_ShouldReturnCreatedCourse() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Java Programming");
        request.setDescription("Learn Java from scratch");
        request.setLevel(CourseLevel.BEGINNER);
        request.setProfessorId(1L);
        request.setPrice(new BigDecimal("49.99"));

        when(courseService.createCourse(any(CreateCourseRequest.class))).thenReturn(courseResponse);

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java Programming"));

        verify(courseService, times(1)).createCourse(any(CreateCourseRequest.class));
    }

    @Test
    @DisplayName("GET /courses/{id} - should get course by ID")
    void getCourseById_ShouldReturnCourse() throws Exception {
        when(courseService.getCourseById(1L)).thenReturn(courseResponse);

        mockMvc.perform(get("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java Programming"));

        verify(courseService, times(1)).getCourseById(1L);
    }

    @Test
    @DisplayName("GET /courses - should get all courses")
    void getAllCourses_ShouldReturnPageOfCourses() throws Exception {
        List<CourseSummaryResponse> courses = Arrays.asList(courseSummary);
        Page<CourseSummaryResponse> page = new PageImpl<>(courses);

        when(courseService.getAllCourses(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(courseService, times(1)).getAllCourses(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /courses/published - should get published courses")
    void getPublishedCourses_ShouldReturnPublishedCourses() throws Exception {
        List<CourseSummaryResponse> courses = Arrays.asList(courseSummary);
        Page<CourseSummaryResponse> page = new PageImpl<>(courses);

        when(courseService.getPublishedCourses(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/courses/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PUBLISHED"));

        verify(courseService, times(1)).getPublishedCourses(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /courses/search - should search courses")
    void searchCourses_ShouldReturnMatchingCourses() throws Exception {
        List<CourseSummaryResponse> courses = Arrays.asList(courseSummary);
        Page<CourseSummaryResponse> page = new PageImpl<>(courses);

        when(courseService.searchCourses(eq("Java"), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/courses/search")
                .param("keyword", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Java Programming"));

        verify(courseService, times(1)).searchCourses(eq("Java"), any(), any(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /courses/{id} - should update course")
    void updateCourse_ShouldReturnUpdatedCourse() throws Exception {
        UpdateCourseRequest request = new UpdateCourseRequest();
        request.setTitle("Advanced Java");

        CourseResponse updated = CourseResponse.builder()
                .id(1L)
                .title("Advanced Java")
                .build();

        when(courseService.updateCourse(eq(1L), any(UpdateCourseRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Advanced Java"));

        verify(courseService, times(1)).updateCourse(eq(1L), any(UpdateCourseRequest.class));
    }

    @Test
    @DisplayName("DELETE /courses/{id} - should delete course")
    void deleteCourse_ShouldReturnNoContent() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/courses/1"))
                .andExpect(status().isNoContent());

        verify(courseService, times(1)).deleteCourse(1L);
    }

    @Test
    @DisplayName("POST /courses/{id}/publish - should publish course")
    void publishCourse_ShouldReturnPublishedCourse() throws Exception {
        CourseResponse published = CourseResponse.builder()
                .id(1L)
                .status(CourseStatus.PUBLISHED)
                .build();

        when(courseService.publishCourse(1L)).thenReturn(published);

        mockMvc.perform(post("/courses/1/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        verify(courseService, times(1)).publishCourse(1L);
    }

    @Test
    @DisplayName("POST /courses/{id}/archive - should archive course")
    void archiveCourse_ShouldReturnArchivedCourse() throws Exception {
        CourseResponse archived = CourseResponse.builder()
                .id(1L)
                .status(CourseStatus.ARCHIVED)
                .build();

        when(courseService.archiveCourse(1L)).thenReturn(archived);

        mockMvc.perform(post("/courses/1/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));

        verify(courseService, times(1)).archiveCourse(1L);
    }

    @Test
    @DisplayName("GET /courses/{id}/lesson-count - should get total lesson count")
    void getTotalLessonCount_ShouldReturnCount() throws Exception {
        when(courseService.getTotalLessonCount(1L)).thenReturn(15);

        mockMvc.perform(get("/courses/1/lesson-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));

        verify(courseService, times(1)).getTotalLessonCount(1L);
    }
}
