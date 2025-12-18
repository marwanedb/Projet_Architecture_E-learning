package e_learning.student_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import e_learning.student_service.dto.*;
import e_learning.student_service.services.StudentService;
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

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentResponse studentResponse;

    @BeforeEach
    void setUp() {
        studentResponse = StudentResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .authId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /students - should create new student")
    void createStudent_ShouldReturnCreatedStudent() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setAuthId(1L);

        when(studentService.createStudent(any(CreateStudentRequest.class))).thenReturn(studentResponse);

        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(studentService, times(1)).createStudent(any(CreateStudentRequest.class));
    }

    @Test
    @DisplayName("GET /students/{id} - should get student by ID")
    void getStudentById_ShouldReturnStudent() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(studentResponse);

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    @DisplayName("GET /students - should get all students")
    void getAllStudents_ShouldReturnPageOfStudents() throws Exception {
        List<StudentResponse> students = Arrays.asList(studentResponse);
        Page<StudentResponse> page = new PageImpl<>(students);

        when(studentService.getAllStudents(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(studentService, times(1)).getAllStudents(any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /students/{id} - should update student")
    void updateStudent_ShouldReturnUpdatedStudent() throws Exception {
        UpdateStudentRequest request = new UpdateStudentRequest();
        request.setFirstName("Jane");

        StudentResponse updated = StudentResponse.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        when(studentService.updateStudent(eq(1L), any(UpdateStudentRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));

        verify(studentService, times(1)).updateStudent(eq(1L), any(UpdateStudentRequest.class));
    }

    @Test
    @DisplayName("DELETE /students/{id} - should delete student")
    void deleteStudent_ShouldReturnNoContent() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/students/1"))
                .andExpect(status().isNoContent());

        verify(studentService, times(1)).deleteStudent(1L);
    }

    @Test
    @DisplayName("GET /students/auth/{authId} - should get student by auth ID")
    void getStudentByAuthId_ShouldReturnStudent() throws Exception {
        when(studentService.getStudentByAuthId(1L)).thenReturn(studentResponse);

        mockMvc.perform(get("/students/auth/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authId").value(1));

        verify(studentService, times(1)).getStudentByAuthId(1L);
    }

    @Test
    @DisplayName("GET /students/email/{email} - should get student by email")
    void getStudentByEmail_ShouldReturnStudent() throws Exception {
        when(studentService.getStudentByEmail("john.doe@example.com")).thenReturn(studentResponse);

        mockMvc.perform(get("/students/email/john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(studentService, times(1)).getStudentByEmail("john.doe@example.com");
    }
}
