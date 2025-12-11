package e_learning.learning_service.exceptions;

public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(Long id) {
        super("Enrollment not found with id: " + id);
    }
}
