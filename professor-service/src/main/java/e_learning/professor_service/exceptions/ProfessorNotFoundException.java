package e_learning.professor_service.exceptions;

public class ProfessorNotFoundException extends RuntimeException {
    public ProfessorNotFoundException(Long id) {
        super("Professor not found with id: " + id);
    }

    public ProfessorNotFoundException(String field, String value) {
        super("Professor not found with " + field + ": " + value);
    }
}
