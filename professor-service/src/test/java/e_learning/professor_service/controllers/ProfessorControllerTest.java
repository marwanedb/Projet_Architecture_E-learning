package e_learning.professor_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import e_learning.professor_service.dto.*;
import e_learning.professor_service.services.ProfessorService;
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

@WebMvcTest(ProfessorController.class)
class ProfessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfessorService professorService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProfessorResponse professorResponse;

    @BeforeEach
    void setUp() {
        professorResponse = ProfessorResponse.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .department("Computer Science")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /professors - should create new professor")
    void createProfessor_ShouldReturnCreatedProfessor() throws Exception {
        CreateProfessorRequest request = new CreateProfessorRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");
        request.setDepartment("Computer Science");

        when(professorService.createProfessor(any(CreateProfessorRequest.class))).thenReturn(professorResponse);

        mockMvc.perform(post("/professors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.department").value("Computer Science"));

        verify(professorService, times(1)).createProfessor(any(CreateProfessorRequest.class));
    }

    @Test
    @DisplayName("GET /professors/{id} - should get professor by ID")
    void getProfessorById_ShouldReturnProfessor() throws Exception {
        when(professorService.getProfessorById(1L)).thenReturn(professorResponse);

        mockMvc.perform(get("/professors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"));

        verify(professorService, times(1)).getProfessorById(1L);
    }

    @Test
    @DisplayName("GET /professors - should get all professors")
    void getAllProfessors_ShouldReturnPageOfProfessors() throws Exception {
        List<ProfessorResponse> professors = Arrays.asList(professorResponse);
        Page<ProfessorResponse> page = new PageImpl<>(professors);

        when(professorService.getAllProfessors(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/professors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(professorService, times(1)).getAllProfessors(any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /professors/{id} - should update professor")
    void updateProfessor_ShouldReturnUpdatedProfessor() throws Exception {
        UpdateProfessorRequest request = new UpdateProfessorRequest();
        request.setDepartment("Data Science");

        ProfessorResponse updated = ProfessorResponse.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .department("Data Science")
                .build();

        when(professorService.updateProfessor(eq(1L), any(UpdateProfessorRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/professors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("Data Science"));

        verify(professorService, times(1)).updateProfessor(eq(1L), any(UpdateProfessorRequest.class));
    }

    @Test
    @DisplayName("DELETE /professors/{id} - should delete professor")
    void deleteProfessor_ShouldReturnNoContent() throws Exception {
        doNothing().when(professorService).deleteProfessor(1L);

        mockMvc.perform(delete("/professors/1"))
                .andExpect(status().isNoContent());

        verify(professorService, times(1)).deleteProfessor(1L);
    }

    @Test
    @DisplayName("GET /professors/department/{department} - should get professors by department")
    void getProfessorsByDepartment_ShouldReturnProfessors() throws Exception {
        List<ProfessorResponse> professors = Arrays.asList(professorResponse);

        when(professorService.getProfessorsByDepartment("Computer Science")).thenReturn(professors);

        mockMvc.perform(get("/professors/department/Computer Science"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].department").value("Computer Science"));

        verify(professorService, times(1)).getProfessorsByDepartment("Computer Science");
    }

    @Test
    @DisplayName("GET /professors/{id}/courses - should get professor courses")
    void getProfessorCourses_ShouldReturnCourses() throws Exception {
        CourseSummaryResponse course = CourseSummaryResponse.builder()
                .id(1L)
                .title("Java Programming")
                .build();
        List<CourseSummaryResponse> courses = Arrays.asList(course);

        when(professorService.getProfessorCourses(1L)).thenReturn(courses);

        mockMvc.perform(get("/professors/1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Java Programming"));

        verify(professorService, times(1)).getProfessorCourses(1L);
    }
}
