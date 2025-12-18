package e_learning.catalog_service.services;

import e_learning.catalog_service.dto.ReviewRequest;
import e_learning.catalog_service.dto.ReviewResponse;
import e_learning.catalog_service.entities.Course;
import e_learning.catalog_service.entities.Review;
import e_learning.catalog_service.exceptions.DuplicateResourceException;
import e_learning.catalog_service.exceptions.ResourceNotFoundException;
import e_learning.catalog_service.repositories.CourseRepository;
import e_learning.catalog_service.repositories.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;

    public ReviewService(ReviewRepository reviewRepository, CourseRepository courseRepository) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        // Check if student already reviewed this course
        if (reviewRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())) {
            throw new DuplicateResourceException("Student has already reviewed this course");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", request.getCourseId()));

        Review review = Review.builder()
                .studentId(request.getStudentId())
                .course(course)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);

        // Update course average rating
        updateCourseAverageRating(course.getId());

        return mapToResponse(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));

        // Verify ownership
        if (!review.getStudentId().equals(request.getStudentId())) {
            throw new IllegalStateException("You can only update your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review = reviewRepository.save(review);

        // Update course average rating
        updateCourseAverageRating(review.getCourse().getId());

        return mapToResponse(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long studentId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));

        // Verify ownership (or admin - could be enhanced)
        if (!review.getStudentId().equals(studentId)) {
            throw new IllegalStateException("You can only delete your own reviews");
        }

        Long courseId = review.getCourse().getId();
        reviewRepository.delete(review);

        // Update course average rating
        updateCourseAverageRating(courseId);
    }

    public Page<ReviewResponse> getCourseReviews(Long courseId, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", courseId);
        }
        return reviewRepository.findByCourseId(courseId, pageable)
                .map(this::mapToResponse);
    }

    public List<ReviewResponse> getStudentReviews(Long studentId) {
        return reviewRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", reviewId));
        return mapToResponse(review);
    }

    private void updateCourseAverageRating(Long courseId) {
        Double averageRating = reviewRepository.calculateAverageRating(courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
        course.setAverageRating(averageRating != null ? averageRating : 0.0);
        courseRepository.save(course);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .studentId(review.getStudentId())
                .courseId(review.getCourse().getId())
                .courseTitle(review.getCourse().getTitle())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
