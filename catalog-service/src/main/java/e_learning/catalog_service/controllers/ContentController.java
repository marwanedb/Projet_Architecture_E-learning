package e_learning.catalog_service.controllers;

import e_learning.catalog_service.entities.Course;
import e_learning.catalog_service.entities.Module;
import e_learning.catalog_service.entities.Lesson;
import e_learning.catalog_service.entities.LessonType;
import e_learning.catalog_service.entities.Quiz;
import e_learning.catalog_service.entities.Question;
import e_learning.catalog_service.entities.Answer;
import e_learning.catalog_service.exceptions.ResourceNotFoundException;
import e_learning.catalog_service.repositories.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/content")
@Tag(name = "Content", description = "Module, Lesson, and Quiz management APIs")
public class ContentController {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public ContentController(CourseRepository courseRepository, ModuleRepository moduleRepository,
            LessonRepository lessonRepository, QuizRepository quizRepository,
            QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    // ==================== MODULE ENDPOINTS ====================

    @PostMapping("/courses/{courseId}/modules")
    @Operation(summary = "Add a module to a course")
    public ResponseEntity<Module> addModule(@PathVariable Long courseId, @RequestBody Module module) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        module.setCourse(course);
        return ResponseEntity.ok(moduleRepository.save(module));
    }

    @PutMapping("/modules/{moduleId}")
    @Operation(summary = "Update a module")
    public ResponseEntity<Module> updateModule(@PathVariable Long moduleId, @RequestBody Module moduleUpdate) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));

        if (moduleUpdate.getTitle() != null)
            module.setTitle(moduleUpdate.getTitle());
        if (moduleUpdate.getDescription() != null)
            module.setDescription(moduleUpdate.getDescription());
        if (moduleUpdate.getOrderIndex() != null)
            module.setOrderIndex(moduleUpdate.getOrderIndex());

        return ResponseEntity.ok(moduleRepository.save(module));
    }

    @DeleteMapping("/modules/{moduleId}")
    @Operation(summary = "Delete a module")
    public ResponseEntity<Void> deleteModule(@PathVariable Long moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException("Module", moduleId);
        }
        moduleRepository.deleteById(moduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/courses/{courseId}/modules")
    @Operation(summary = "Get all modules for a course")
    public ResponseEntity<List<Module>> getModulesByCourse(@PathVariable Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", courseId);
        }
        return ResponseEntity.ok(moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId));
    }

    // ==================== LESSON ENDPOINTS ====================

    @PostMapping(value = "/modules/{moduleId}/lessons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add a lesson to a module (with optional file upload)")
    public ResponseEntity<Lesson> addLesson(
            @PathVariable Long moduleId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("type") LessonType type,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "url", required = false) String urlLink,
            @RequestParam(value = "durationMinutes", required = false) Integer durationMinutes,
            @RequestParam(value = "orderIndex", required = false) Integer orderIndex) throws IOException {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));

        Lesson lesson = new Lesson();
        lesson.setTitle(title);
        lesson.setDescription(description);
        lesson.setType(type);
        lesson.setModule(module);
        lesson.setDurationMinutes(durationMinutes);
        lesson.setOrderIndex(orderIndex);

        if (file != null && !file.isEmpty()) {
            String storageUrl = saveFile(file);
            lesson.setUrl(storageUrl);
        } else if (urlLink != null && !urlLink.isEmpty()) {
            lesson.setUrl(urlLink);
        } else {
            throw new IllegalArgumentException("You must provide either a file or a URL");
        }

        return ResponseEntity.ok(lessonRepository.save(lesson));
    }

    @PutMapping("/lessons/{lessonId}")
    @Operation(summary = "Update a lesson")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long lessonId, @RequestBody Lesson lessonUpdate) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));

        if (lessonUpdate.getTitle() != null)
            lesson.setTitle(lessonUpdate.getTitle());
        if (lessonUpdate.getDescription() != null)
            lesson.setDescription(lessonUpdate.getDescription());
        if (lessonUpdate.getType() != null)
            lesson.setType(lessonUpdate.getType());
        if (lessonUpdate.getUrl() != null)
            lesson.setUrl(lessonUpdate.getUrl());
        if (lessonUpdate.getDurationMinutes() != null)
            lesson.setDurationMinutes(lessonUpdate.getDurationMinutes());
        if (lessonUpdate.getOrderIndex() != null)
            lesson.setOrderIndex(lessonUpdate.getOrderIndex());

        return ResponseEntity.ok(lessonRepository.save(lesson));
    }

    @DeleteMapping("/lessons/{lessonId}")
    @Operation(summary = "Delete a lesson")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new ResourceNotFoundException("Lesson", lessonId);
        }
        lessonRepository.deleteById(lessonId);
        return ResponseEntity.noContent().build();
    }

    // ==================== QUIZ ENDPOINTS ====================

    @PostMapping("/modules/{moduleId}/quizzes")
    @Operation(summary = "Add a quiz to a module")
    public ResponseEntity<Quiz> addQuiz(@PathVariable Long moduleId, @RequestBody Quiz quiz) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", moduleId));
        quiz.setModule(module);
        return ResponseEntity.ok(quizRepository.save(quiz));
    }

    @PutMapping("/quizzes/{quizId}")
    @Operation(summary = "Update a quiz")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long quizId, @RequestBody Quiz quizUpdate) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", quizId));

        if (quizUpdate.getTitle() != null)
            quiz.setTitle(quizUpdate.getTitle());
        if (quizUpdate.getDescription() != null)
            quiz.setDescription(quizUpdate.getDescription());
        if (quizUpdate.getPassingScore() != null)
            quiz.setPassingScore(quizUpdate.getPassingScore());
        if (quizUpdate.getTimeLimitMinutes() != null)
            quiz.setTimeLimitMinutes(quizUpdate.getTimeLimitMinutes());

        return ResponseEntity.ok(quizRepository.save(quiz));
    }

    @DeleteMapping("/quizzes/{quizId}")
    @Operation(summary = "Delete a quiz")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz", quizId);
        }
        quizRepository.deleteById(quizId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quizzes/{quizId}")
    @Operation(summary = "Get a quiz with questions")
    public ResponseEntity<Quiz> getQuiz(@PathVariable Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", quizId));
        return ResponseEntity.ok(quiz);
    }

    // ==================== QUESTION ENDPOINTS ====================

    @PostMapping("/quizzes/{quizId}/questions")
    @Operation(summary = "Add a question to a quiz")
    public ResponseEntity<Question> addQuestion(@PathVariable Long quizId, @RequestBody Question question) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", quizId));
        question.setQuiz(quiz);
        return ResponseEntity.ok(questionRepository.save(question));
    }

    @PutMapping("/questions/{questionId}")
    @Operation(summary = "Update a question")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long questionId,
            @RequestBody Question questionUpdate) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));

        if (questionUpdate.getQuestionText() != null)
            question.setQuestionText(questionUpdate.getQuestionText());
        if (questionUpdate.getType() != null)
            question.setType(questionUpdate.getType());
        if (questionUpdate.getPoints() != null)
            question.setPoints(questionUpdate.getPoints());

        return ResponseEntity.ok(questionRepository.save(question));
    }

    @DeleteMapping("/questions/{questionId}")
    @Operation(summary = "Delete a question")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question", questionId);
        }
        questionRepository.deleteById(questionId);
        return ResponseEntity.noContent().build();
    }

    // ==================== ANSWER ENDPOINTS ====================

    @PostMapping("/questions/{questionId}/answers")
    @Operation(summary = "Add an answer to a question")
    public ResponseEntity<Answer> addAnswer(@PathVariable Long questionId, @RequestBody Answer answer) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));
        answer.setQuestion(question);
        return ResponseEntity.ok(answerRepository.save(answer));
    }

    @PutMapping("/answers/{answerId}")
    @Operation(summary = "Update an answer")
    public ResponseEntity<Answer> updateAnswer(@PathVariable Long answerId, @RequestBody Answer answerUpdate) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", answerId));

        if (answerUpdate.getAnswerText() != null)
            answer.setAnswerText(answerUpdate.getAnswerText());
        answer.setCorrect(answerUpdate.isCorrect());

        return ResponseEntity.ok(answerRepository.save(answer));
    }

    @DeleteMapping("/answers/{answerId}")
    @Operation(summary = "Delete an answer")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long answerId) {
        if (!answerRepository.existsById(answerId)) {
            throw new ResourceNotFoundException("Answer", answerId);
        }
        answerRepository.deleteById(answerId);
        return ResponseEntity.noContent().build();
    }

    // ==================== UTILITY ====================

    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uploadDir + fileName;
    }
}
