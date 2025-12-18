package e_learning.learning_service.services;

import e_learning.learning_service.clients.CatalogClient;
import e_learning.learning_service.clients.StudentClient;
import e_learning.learning_service.dto.CertificateResponse;
import e_learning.learning_service.dto.CourseSummaryResponse;
import e_learning.learning_service.dto.StudentSummaryResponse;
import e_learning.learning_service.entities.Certificate;
import e_learning.learning_service.entities.Enrollment;
import e_learning.learning_service.entities.EnrollmentStatus;
import e_learning.learning_service.exceptions.EnrollmentNotFoundException;
import e_learning.learning_service.repositories.CertificateRepository;
import e_learning.learning_service.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentClient studentClient;
    private final CatalogClient catalogClient;

    public CertificateService(CertificateRepository certificateRepository,
            EnrollmentRepository enrollmentRepository,
            StudentClient studentClient,
            CatalogClient catalogClient) {
        this.certificateRepository = certificateRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentClient = studentClient;
        this.catalogClient = catalogClient;
    }

    @Transactional
    public CertificateResponse generateCertificate(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(enrollmentId));

        // Check if course is completed
        if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            throw new IllegalStateException("Certificate can only be generated for completed courses");
        }

        // Check if certificate already exists
        if (certificateRepository.existsByEnrollmentId(enrollmentId)) {
            return getCertificateByEnrollment(enrollmentId);
        }

        // Fetch student and course details
        String studentName = "Unknown Student";
        String courseTitle = "Unknown Course";

        try {
            StudentSummaryResponse student = studentClient.getStudentById(enrollment.getStudentId());
            studentName = student.getFirstName() + " " + student.getLastName();
        } catch (Exception e) {
            // Continue with default
        }

        try {
            CourseSummaryResponse course = catalogClient.getCourseById(enrollment.getCourseId());
            courseTitle = course.getTitle();
        } catch (Exception e) {
            // Continue with default
        }

        // Generate unique certificate code
        String certificateCode = generateCertificateCode();

        Certificate certificate = Certificate.builder()
                .enrollment(enrollment)
                .certificateCode(certificateCode)
                .studentName(studentName)
                .courseTitle(courseTitle)
                .build();

        certificate = certificateRepository.save(certificate);
        return mapToResponse(certificate);
    }

    public CertificateResponse getCertificateByEnrollment(Long enrollmentId) {
        Certificate certificate = certificateRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new IllegalStateException("Certificate not found for this enrollment"));
        return mapToResponse(certificate);
    }

    public CertificateResponse verifyCertificate(String certificateCode) {
        Certificate certificate = certificateRepository.findByCertificateCode(certificateCode)
                .orElseThrow(() -> new IllegalStateException("Invalid certificate code"));
        return mapToResponse(certificate);
    }

    public List<CertificateResponse> getStudentCertificates(Long studentId) {
        return certificateRepository.findByEnrollmentStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private String generateCertificateCode() {
        // Generate a unique, user-friendly code
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "CERT-" + uuid.substring(0, 12);
    }

    private CertificateResponse mapToResponse(Certificate certificate) {
        return CertificateResponse.builder()
                .id(certificate.getId())
                .enrollmentId(certificate.getEnrollment().getId())
                .studentId(certificate.getEnrollment().getStudentId())
                .courseId(certificate.getEnrollment().getCourseId())
                .studentName(certificate.getStudentName())
                .courseTitle(certificate.getCourseTitle())
                .certificateCode(certificate.getCertificateCode())
                .issuedAt(certificate.getIssuedAt())
                .verificationUrl("/learning-service/certificates/verify/" + certificate.getCertificateCode())
                .build();
    }
}
