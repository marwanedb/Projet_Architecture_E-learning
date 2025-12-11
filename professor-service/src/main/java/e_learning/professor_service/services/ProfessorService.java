package e_learning.professor_service.services;

import e_learning.professor_service.dto.*;
import e_learning.professor_service.entities.Professor;
import e_learning.professor_service.exceptions.*;
import e_learning.professor_service.repositories.ProfessorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final e_learning.professor_service.clients.AuthClient authClient;
    private final e_learning.professor_service.clients.CatalogClient catalogClient;

    public ProfessorService(ProfessorRepository professorRepository,
            e_learning.professor_service.clients.AuthClient authClient,
            e_learning.professor_service.clients.CatalogClient catalogClient) {
        this.professorRepository = professorRepository;
        this.authClient = authClient;
        this.catalogClient = catalogClient;
    }

    @Transactional
    public ProfessorResponse createProfessor(CreateProfessorRequest request) {
        // Validate Auth
        try {
            authClient.getUserById(request.getAuthId());
        } catch (Exception e) {
            throw new ProfessorNotFoundException("authId", request.getAuthId().toString());
        }

        if (professorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateProfessorException("Professor with email '" + request.getEmail() + "' already exists");
        }
        if (professorRepository.existsByAuthId(request.getAuthId())) {
            throw new DuplicateProfessorException("Professor profile already exists for this auth account");
        }

        Professor professor = Professor.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .department(request.getDepartment())
                .specialization(request.getSpecialization())
                .bio(request.getBio())
                .profilePictureUrl(request.getProfilePictureUrl())
                .officeLocation(request.getOfficeLocation())
                .authId(request.getAuthId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        professor = professorRepository.save(professor);
        return mapToResponse(professor);
    }

    @Transactional
    public ProfessorResponse updateProfessor(Long id, UpdateProfessorRequest request) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ProfessorNotFoundException(id));

        if (request.getFirstName() != null)
            professor.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            professor.setLastName(request.getLastName());
        if (request.getDepartment() != null)
            professor.setDepartment(request.getDepartment());
        if (request.getSpecialization() != null)
            professor.setSpecialization(request.getSpecialization());
        if (request.getBio() != null)
            professor.setBio(request.getBio());
        if (request.getProfilePictureUrl() != null)
            professor.setProfilePictureUrl(request.getProfilePictureUrl());
        if (request.getOfficeLocation() != null)
            professor.setOfficeLocation(request.getOfficeLocation());

        professor.setUpdatedAt(LocalDateTime.now());
        professor = professorRepository.save(professor);
        return mapToResponse(professor);
    }

    @Transactional
    public void deleteProfessor(Long id) {
        if (!professorRepository.existsById(id)) {
            throw new ProfessorNotFoundException(id);
        }
        professorRepository.deleteById(id);
    }

    public ProfessorResponse getProfessorById(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ProfessorNotFoundException(id));
        return mapToResponse(professor);
    }

    public ProfessorResponse getProfessorByAuthId(Long authId) {
        Professor professor = professorRepository.findByAuthId(authId)
                .orElseThrow(() -> new ProfessorNotFoundException("authId", authId.toString()));
        return mapToResponse(professor);
    }

    public ProfessorResponse getProfessorByEmail(String email) {
        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() -> new ProfessorNotFoundException("email", email));
        return mapToResponse(professor);
    }

    public List<ProfessorResponse> getProfessorsByDepartment(String department) {
        return professorRepository.findByDepartment(department).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<ProfessorResponse> getAllProfessors(Pageable pageable) {
        return professorRepository.findAll(pageable).map(this::mapToResponse);
    }

    public List<ProfessorResponse> getAllProfessors() {
        return professorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<Object> getProfessorCourses(Long professorId) {
        // Verify professor exists first
        if (!professorRepository.existsById(professorId)) {
            throw new ProfessorNotFoundException(professorId);
        }
        return catalogClient.getCoursesByProfessor(professorId);
    }

    private ProfessorResponse mapToResponse(Professor professor) {
        return ProfessorResponse.builder()
                .id(professor.getId())
                .firstName(professor.getFirstName())
                .lastName(professor.getLastName())
                .email(professor.getEmail())
                .department(professor.getDepartment())
                .specialization(professor.getSpecialization())
                .bio(professor.getBio())
                .profilePictureUrl(professor.getProfilePictureUrl())
                .officeLocation(professor.getOfficeLocation())
                .createdAt(professor.getCreatedAt())
                .authId(professor.getAuthId())
                .build();
    }
}
