package e_learning.learning_service.controllers;

import e_learning.learning_service.dto.CertificateResponse;
import e_learning.learning_service.services.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certificates")
@Tag(name = "Certificates", description = "Course completion certificate management")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/generate/{enrollmentId}")
    @Operation(summary = "Generate a certificate for a completed course enrollment")
    public ResponseEntity<CertificateResponse> generateCertificate(@PathVariable Long enrollmentId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.generateCertificate(enrollmentId));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    @Operation(summary = "Get certificate by enrollment ID")
    public ResponseEntity<CertificateResponse> getCertificateByEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(certificateService.getCertificateByEnrollment(enrollmentId));
    }

    @GetMapping("/verify/{certificateCode}")
    @Operation(summary = "Verify a certificate by its unique code")
    public ResponseEntity<CertificateResponse> verifyCertificate(@PathVariable String certificateCode) {
        return ResponseEntity.ok(certificateService.verifyCertificate(certificateCode));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all certificates for a student")
    public ResponseEntity<List<CertificateResponse>> getStudentCertificates(@PathVariable Long studentId) {
        return ResponseEntity.ok(certificateService.getStudentCertificates(studentId));
    }
}
