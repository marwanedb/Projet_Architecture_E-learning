package e_learning.professor_service.exceptions;

public class DuplicateProfessorException extends RuntimeException {
    public DuplicateProfessorException(String message) {
        super(message);
    }
}
