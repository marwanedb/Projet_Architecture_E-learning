package e_learning.admin_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {
    private long totalStudents;
    private long totalProfessors;
    private long totalCourses;
    private long totalEnrollments;
    // We could add revenue here if we had pricing models
}
