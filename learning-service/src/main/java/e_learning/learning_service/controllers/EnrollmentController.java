package e_learning.learning_service.controllers;


import e_learning.learning_service.clients.CourseRestClient;
import e_learning.learning_service.entities.Enrollment;
import e_learning.learning_service.repositories.EnrollmentRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRestClient courseRestClient; // Injection du client Feign

    public EnrollmentController(EnrollmentRepository enrollmentRepository, CourseRestClient courseRestClient) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRestClient = courseRestClient;
    }

    @PostMapping
    public Enrollment enrollStudent(@RequestBody Enrollment enrollment) {
        // 1. Vérifier si le cours existe via l'appel réseau (Microservice à Microservice)
        // Si le cours n'existe pas, Feign lancera une erreur 404
        Object course = courseRestClient.getCourseById(enrollment.getCourseId());

        // 2. Si OK, on sauvegarde
        enrollment.setEnrollmentDate(LocalDate.now());
        return enrollmentRepository.save(enrollment);
    }

    @GetMapping("/{id}")
    public Enrollment getEnrollment(@PathVariable Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow();
        // On enrichit l'objet avec les détails du cours récupérés en temps réel
        enrollment.setCourseDetails(courseRestClient.getCourseById(enrollment.getCourseId()));
        return enrollment;
    }
}