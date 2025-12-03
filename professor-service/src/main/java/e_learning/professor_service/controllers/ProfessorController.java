package e_learning.professor_service.controllers;


import e_learning.professor_service.entities.Professor;
import e_learning.professor_service.repositories.ProfessorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professors")
public class ProfessorController {

    private final ProfessorRepository professorRepository;

    public ProfessorController(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @PostMapping
    public Professor createProfessor(@RequestBody Professor professor) {
        return professorRepository.save(professor);
    }

    @GetMapping
    public List<Professor> getAllProfessors() {
        return professorRepository.findAll();
    }
}
