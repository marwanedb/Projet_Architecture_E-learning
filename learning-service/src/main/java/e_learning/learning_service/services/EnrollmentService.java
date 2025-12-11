package e_learning.learning_service.services;

import e_learning.learning_service.clients.CatalogClient;
import e_learning.learning_service.clients.StudentClient;
import e_learning.learning_service.dto.*;
import e_learning.learning_service.entities.Enrollment;
import e_learning.learning_service.entities.EnrollmentStatus;
import e_learning.learning_service.exceptions.*;
import e_learning.learning_service.repositories.EnrollmentRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CatalogClient catalogClient;
    private final StudentClient studentClient;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CatalogClient catalogClient,
            StudentClient studentClient) {
        this.enrollmentRepository = enrollmentRepository;
        this.catalogClient = catalogClient;
        this.studentClient = studentClient;
    }

    @Transactional
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        // Validation via Feign
        try {
            studentClient.getStudentById(request.getStudentId());
        } catch (FeignException.NotFound e) {
            throw new ServiceCommunicationException("Student not found with ID: " + request.getStudentId());
        }

        try {
            catalogClient.getCourseById(request.getCourseId());
        } catch (FeignException.NotFound e) {
            throw new ServiceCommunicationException("Course not found with ID: " + request.getCourseId());
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())) {
            throw new DuplicateEnrollmentException("Student is already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .status(EnrollmentStatus.ACTIVE)
                .build();

        enrollment = enrollmentRepository.save(enrollment);
        return mapToResponse(enrollment, true); // Fetch details
    }

    public List<EnrollmentResponse> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(e -> mapToResponse(e, true)) // Fetch details for each
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponse> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(e -> mapToResponse(e, false)) // Don't fetch course details as known
                .collect(Collectors.toList());
    }

    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException(id));
        return mapToResponse(enrollment, true);
    }

    // Helper to map and optionally enrich with external data
    private EnrollmentResponse mapToResponse(Enrollment enrollment, boolean fetchDetails) {
        String courseTitle = "Unknown Course";
        String studentName = "Unknown Student";

        if (fetchDetails) {
            try {
                // Best effort enrichment
                CourseSummaryResponse course = catalogClient.getCourseById(enrollment.getCourseId());
                courseTitle = course.getTitle();

                StudentSummaryResponse student = studentClient.getStudentById(enrollment.getStudentId());
                studentName = student.getFirstName() + " " + student.getLastName();
            } catch (Exception e) {
                // Log and continue without details
            }
        }

        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .courseTitle(courseTitle)
                .studentName(studentName)
                .status(enrollment.getStatus())
                .progress(enrollment.getProgress())
                .enrolledAt(enrollment.getEnrolledAt())
                .completedAt(enrollment.getCompletedAt())
                .build();
    }
}
