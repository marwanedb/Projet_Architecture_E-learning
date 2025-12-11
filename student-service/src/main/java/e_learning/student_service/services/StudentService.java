package e_learning.student_service.services;

import e_learning.student_service.dto.*;
import e_learning.student_service.entities.Student;
import e_learning.student_service.exceptions.*;
import e_learning.student_service.repositories.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final e_learning.student_service.clients.AuthClient authClient;

    public StudentService(StudentRepository studentRepository,
            e_learning.student_service.clients.AuthClient authClient) {
        this.studentRepository = studentRepository;
        this.authClient = authClient;
    }

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        // Validation via Auth Service
        try {
            authClient.getUserById(request.getAuthId());
        } catch (Exception e) {
            throw new StudentNotFoundException("authId", request.getAuthId().toString());
        }

        // Check for duplicates
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateStudentException("Student with email '" + request.getEmail() + "' already exists");
        }
        if (studentRepository.existsByAuthId(request.getAuthId())) {
            throw new DuplicateStudentException("Student profile already exists for this auth account");
        }
        if (request.getCne() != null && studentRepository.existsByCne(request.getCne())) {
            throw new DuplicateStudentException("Student with CNE '" + request.getCne() + "' already exists");
        }

        Student student = Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .cne(request.getCne())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .profilePictureUrl(request.getProfilePictureUrl())
                .dateOfBirth(request.getDateOfBirth())
                .authId(request.getAuthId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        student = studentRepository.save(student);
        return mapToResponse(student);
    }

    // ... rest of class remains unchanged, using abbreviated replacement

    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        if (request.getFirstName() != null)
            student.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            student.setLastName(request.getLastName());
        if (request.getCne() != null) {
            // Check for duplicate CNE
            studentRepository.findByCne(request.getCne())
                    .filter(s -> !s.getId().equals(id))
                    .ifPresent(s -> {
                        throw new DuplicateStudentException("CNE already in use");
                    });
            student.setCne(request.getCne());
        }
        if (request.getPhoneNumber() != null)
            student.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null)
            student.setAddress(request.getAddress());
        if (request.getProfilePictureUrl() != null)
            student.setProfilePictureUrl(request.getProfilePictureUrl());
        if (request.getDateOfBirth() != null)
            student.setDateOfBirth(request.getDateOfBirth());

        student.setUpdatedAt(LocalDateTime.now());
        student = studentRepository.save(student);
        return mapToResponse(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }

    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        return mapToResponse(student);
    }

    public StudentResponse getStudentByAuthId(Long authId) {
        Student student = studentRepository.findByAuthId(authId)
                .orElseThrow(() -> new StudentNotFoundException("authId", authId.toString()));
        return mapToResponse(student);
    }

    public StudentResponse getStudentByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new StudentNotFoundException("email", email));
        return mapToResponse(student);
    }

    public Page<StudentResponse> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable).map(this::mapToResponse);
    }

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .cne(student.getCne())
                .phoneNumber(student.getPhoneNumber())
                .address(student.getAddress())
                .profilePictureUrl(student.getProfilePictureUrl())
                .dateOfBirth(student.getDateOfBirth())
                .createdAt(student.getCreatedAt())
                .authId(student.getAuthId())
                .build();
    }
}
