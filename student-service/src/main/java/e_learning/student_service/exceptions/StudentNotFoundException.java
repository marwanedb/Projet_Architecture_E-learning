package e_learning.student_service.exceptions;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long id) {
        super("Student not found with id: " + id);
    }

    public StudentNotFoundException(String field, String value) {
        super("Student not found with " + field + ": " + value);
    }
}
