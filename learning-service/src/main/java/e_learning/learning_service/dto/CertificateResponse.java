package e_learning.learning_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateResponse {
    private Long id;
    private Long enrollmentId;
    private Long studentId;
    private Long courseId;
    private String studentName;
    private String courseTitle;
    private String certificateCode;
    private LocalDateTime issuedAt;
    private String verificationUrl;
}
