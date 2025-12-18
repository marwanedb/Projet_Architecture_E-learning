package e_learning.learning_service.controllers;

import e_learning.learning_service.dto.CertificateResponse;
import e_learning.learning_service.services.CertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CertificateController.class)
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificateService certificateService;

    private CertificateResponse certificateResponse;

    @BeforeEach
    void setUp() {
        certificateResponse = CertificateResponse.builder()
                .id(1L)
                .enrollmentId(1L)
                .studentId(1L)
                .courseId(1L)
                .studentName("John Doe")
                .courseTitle("Java Programming")
                .certificateCode("CERT-ABC123DEF456")
                .issuedAt(LocalDateTime.now())
                .verificationUrl("/learning-service/certificates/verify/CERT-ABC123DEF456")
                .build();
    }

    @Test
    @DisplayName("POST /certificates/generate/{enrollmentId} - should generate certificate")
    void generateCertificate_ShouldReturnCertificate() throws Exception {
        when(certificateService.generateCertificate(1L)).thenReturn(certificateResponse);

        mockMvc.perform(post("/certificates/generate/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.certificateCode").value("CERT-ABC123DEF456"))
                .andExpect(jsonPath("$.studentName").value("John Doe"))
                .andExpect(jsonPath("$.courseTitle").value("Java Programming"));

        verify(certificateService, times(1)).generateCertificate(1L);
    }

    @Test
    @DisplayName("GET /certificates/enrollment/{enrollmentId} - should get certificate by enrollment")
    void getCertificateByEnrollment_ShouldReturnCertificate() throws Exception {
        when(certificateService.getCertificateByEnrollment(1L)).thenReturn(certificateResponse);

        mockMvc.perform(get("/certificates/enrollment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(1));

        verify(certificateService, times(1)).getCertificateByEnrollment(1L);
    }

    @Test
    @DisplayName("GET /certificates/verify/{code} - should verify certificate")
    void verifyCertificate_ShouldReturnCertificate() throws Exception {
        when(certificateService.verifyCertificate("CERT-ABC123DEF456")).thenReturn(certificateResponse);

        mockMvc.perform(get("/certificates/verify/CERT-ABC123DEF456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificateCode").value("CERT-ABC123DEF456"))
                .andExpect(jsonPath("$.studentName").exists())
                .andExpect(jsonPath("$.courseTitle").exists());

        verify(certificateService, times(1)).verifyCertificate("CERT-ABC123DEF456");
    }

    @Test
    @DisplayName("GET /certificates/student/{studentId} - should get student certificates")
    void getStudentCertificates_ShouldReturnCertificates() throws Exception {
        List<CertificateResponse> certificates = Arrays.asList(certificateResponse);

        when(certificateService.getStudentCertificates(1L)).thenReturn(certificates);

        mockMvc.perform(get("/certificates/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].studentId").value(1));

        verify(certificateService, times(1)).getStudentCertificates(1L);
    }
}
