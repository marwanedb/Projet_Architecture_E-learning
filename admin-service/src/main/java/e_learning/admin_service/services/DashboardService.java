package e_learning.admin_service.services;

import e_learning.admin_service.clients.*;
import e_learning.admin_service.dto.DashboardStats;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final StudentClient studentClient;
    private final ProfessorClient professorClient;
    private final CatalogClient catalogClient;

    public DashboardService(StudentClient studentClient, ProfessorClient professorClient, CatalogClient catalogClient) {
        this.studentClient = studentClient;
        this.professorClient = professorClient;
        this.catalogClient = catalogClient;
    }

    public DashboardStats getDashboardStats() {
        long totalStudents = 0;
        long totalProfessors = 0;
        long totalCourses = 0;
        long totalEnrollments = 0; // Mocked for now

        try {
            List<Object> students = studentClient.getAllStudents();
            if (students != null)
                totalStudents = students.size();
        } catch (Exception e) {
            // Log error, keep 0
        }

        try {
            List<Object> professors = professorClient.getAllProfessors();
            if (professors != null)
                totalProfessors = professors.size();
        } catch (Exception e) {
            // Log error
        }

        try {
            // Catalog returns a Page object structure, which comes as a map in pure JSON if
            // type not known
            Object coursesResponse = catalogClient.getAllCourses();
            if (coursesResponse instanceof Map) {
                Map<?, ?> page = (Map<?, ?>) coursesResponse;
                Object totalElements = page.get("totalElements");
                if (totalElements instanceof Number) {
                    totalCourses = ((Number) totalElements).longValue();
                } else if (totalElements instanceof String) {
                    totalCourses = Long.parseLong((String) totalElements);
                }
            }
        } catch (Exception e) {
            // Log error
        }

        return DashboardStats.builder()
                .totalStudents(totalStudents)
                .totalProfessors(totalProfessors)
                .totalCourses(totalCourses)
                .totalEnrollments(totalEnrollments)
                .build();
    }
}
