package e_learning.catalog_service.services;

import e_learning.catalog_service.dto.*;
import e_learning.catalog_service.entities.Course;
import e_learning.catalog_service.entities.Category;
import e_learning.catalog_service.entities.Module;
import e_learning.catalog_service.entities.Lesson;
import e_learning.catalog_service.entities.Quiz;
import e_learning.catalog_service.entities.CourseLevel;
import e_learning.catalog_service.entities.CourseStatus;
import e_learning.catalog_service.exceptions.*;
import e_learning.catalog_service.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .level(request.getLevel())
                .status(CourseStatus.DRAFT)
                .professorId(request.getProfessorId())
                .thumbnailUrl(request.getThumbnailUrl())
                .price(request.getPrice())
                .durationHours(request.getDurationHours())
                .averageRating(0.0)
                .totalEnrollments(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            course.setCategory(category);
        }

        course = courseRepository.save(course);
        return mapToResponse(course);
    }

    @Transactional
    public CourseResponse updateCourse(Long id, UpdateCourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));

        if (request.getTitle() != null)
            course.setTitle(request.getTitle());
        if (request.getDescription() != null)
            course.setDescription(request.getDescription());
        if (request.getLevel() != null)
            course.setLevel(request.getLevel());
        if (request.getStatus() != null)
            course.setStatus(request.getStatus());
        if (request.getThumbnailUrl() != null)
            course.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getPrice() != null)
            course.setPrice(request.getPrice());
        if (request.getDurationHours() != null)
            course.setDurationHours(request.getDurationHours());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            course.setCategory(category);
        }

        course.setUpdatedAt(LocalDateTime.now());
        course = courseRepository.save(course);
        return mapToResponse(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course", id);
        }
        courseRepository.deleteById(id);
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        return mapToResponse(course);
    }

    public Page<CourseSummaryResponse> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(this::mapToSummary);
    }

    public Page<CourseSummaryResponse> getPublishedCourses(Pageable pageable) {
        return courseRepository.findAllPublished(pageable).map(this::mapToSummary);
    }

    public Page<CourseSummaryResponse> searchCourses(String keyword, Long categoryId,
            CourseLevel level, CourseStatus status, Pageable pageable) {
        return courseRepository.searchCourses(keyword, categoryId, level, status, pageable)
                .map(this::mapToSummary);
    }

    public List<CourseSummaryResponse> getCoursesByProfessor(Long professorId) {
        return courseRepository.findByProfessorId(professorId).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    public Page<CourseSummaryResponse> getCoursesByCategory(Long categoryId, Pageable pageable) {
        return courseRepository.findByCategoryId(categoryId, pageable).map(this::mapToSummary);
    }

    @Transactional
    public CourseResponse publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        course.setStatus(CourseStatus.PUBLISHED);
        course.setUpdatedAt(LocalDateTime.now());
        course = courseRepository.save(course);
        return mapToResponse(course);
    }

    @Transactional
    public CourseResponse archiveCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        course.setStatus(CourseStatus.ARCHIVED);
        course.setUpdatedAt(LocalDateTime.now());
        course = courseRepository.save(course);
        return mapToResponse(course);
    }

    private CourseResponse mapToResponse(Course course) {
        CourseResponse response = CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .level(course.getLevel())
                .status(course.getStatus())
                .professorId(course.getProfessorId())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .durationHours(course.getDurationHours())
                .averageRating(course.getAverageRating())
                .totalEnrollments(course.getTotalEnrollments())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();

        if (course.getCategory() != null) {
            response.setCategory(CategoryResponse.builder()
                    .id(course.getCategory().getId())
                    .name(course.getCategory().getName())
                    .description(course.getCategory().getDescription())
                    .iconUrl(course.getCategory().getIconUrl())
                    .build());
        }

        if (course.getModules() != null) {
            response.setModules(course.getModules().stream()
                    .map(this::mapModuleToResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private CourseSummaryResponse mapToSummary(Course course) {
        return CourseSummaryResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .level(course.getLevel())
                .status(course.getStatus())
                .professorId(course.getProfessorId())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .durationHours(course.getDurationHours())
                .averageRating(course.getAverageRating())
                .totalEnrollments(course.getTotalEnrollments())
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .moduleCount(course.getModules() != null ? course.getModules().size() : 0)
                .build();
    }

    private ModuleResponse mapModuleToResponse(Module module) {
        return ModuleResponse.builder()
                .id(module.getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .orderIndex(module.getOrderIndex())
                .lessons(module.getLessons() != null
                        ? module.getLessons().stream().map(this::mapLessonToResponse).collect(Collectors.toList())
                        : null)
                .quizzes(module.getQuizzes() != null
                        ? module.getQuizzes().stream().map(this::mapQuizToSummary).collect(Collectors.toList())
                        : null)
                .build();
    }

    private LessonResponse mapLessonToResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .type(lesson.getType())
                .url(lesson.getUrl())
                .durationMinutes(lesson.getDurationMinutes())
                .orderIndex(lesson.getOrderIndex())
                .build();
    }

    private QuizSummaryResponse mapQuizToSummary(Quiz quiz) {
        return QuizSummaryResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .passingScore(quiz.getPassingScore())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .questionCount(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0)
                .build();
    }
}
