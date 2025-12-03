package e_learning.catalog_service.controllers;

import e_learning.catalog_service.entities.Course;
import e_learning.catalog_service.entities.Module;
import e_learning.catalog_service.entities.Lesson;
import e_learning.catalog_service.repositories.CourseRepository;
import e_learning.catalog_service.repositories.ModuleRepository;
import e_learning.catalog_service.repositories.LessonRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/content")
public class ContentController {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;

    public ContentController(CourseRepository c, ModuleRepository m, LessonRepository l) {
        this.courseRepository = c;
        this.moduleRepository = m;
        this.lessonRepository = l;
    }

    // Ajouter un Module à un Cours
    @PostMapping("/courses/{courseId}/modules")
    public Module addModule(@PathVariable Long courseId, @RequestBody Module module) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));

        module.setCourse(course);
        return moduleRepository.save(module);
    }

    // Ajouter une Leçon à un Module
    @PostMapping(value = "/modules/{moduleId}/lessons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Lesson addLesson(
            @PathVariable Long moduleId,
            @RequestParam("title") String title,
            @RequestParam("type") String type, // VIDEO ou PDF
            @RequestParam(value = "file", required = false) MultipartFile file, // Optionnel
            @RequestParam(value = "url", required = false) String urlLink      // Optionnel
    ) throws IOException {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        // Création de l'objet Leçon
        Lesson lesson = new Lesson();
        lesson.setTitle(title);
        lesson.setType(type);
        lesson.setModule(module);

        // LOGIQUE DE DÉCISION : Fichier ou Lien ?
        if (file != null && !file.isEmpty()) {
            // 1. C'est un Upload de fichier
            String storageUrl = saveFile(file);
            lesson.setUrl(storageUrl);
        } else if (urlLink != null && !urlLink.isEmpty()) {
            // 2. C'est un lien YouTube/Externe
            lesson.setUrl(urlLink);
        } else {
            throw new RuntimeException("Vous devez fournir soit un fichier, soit un lien URL !");
        }

        return lessonRepository.save(lesson);
    }

    // --- Méthode utilitaire pour sauvegarder sur le disque ---
    private String saveFile(MultipartFile file) throws IOException {
        // Définir le dossier de stockage (à la racine du projet)
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);

        // Créer le dossier s'il n'existe pas
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom unique pour éviter d'écraser des fichiers (ex: video_xyz123.mp4)
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Copier le fichier
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retourner le chemin relatif
        return uploadDir + fileName;
    }
}
