package e_learning.student_service.services;

import e_learning.student_service.dto.WishlistResponse;
import e_learning.student_service.entities.Student;
import e_learning.student_service.entities.Wishlist;
import e_learning.student_service.exceptions.StudentNotFoundException;
import e_learning.student_service.repositories.StudentRepository;
import e_learning.student_service.repositories.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final StudentRepository studentRepository;

    public WishlistService(WishlistRepository wishlistRepository, StudentRepository studentRepository) {
        this.wishlistRepository = wishlistRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public WishlistResponse addToWishlist(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Check if already in wishlist
        if (wishlistRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            return wishlistRepository.findByStudentIdAndCourseId(studentId, courseId)
                    .map(this::mapToResponse)
                    .orElse(null);
        }

        Wishlist wishlist = Wishlist.builder()
                .student(student)
                .courseId(courseId)
                .build();

        wishlist = wishlistRepository.save(wishlist);
        return mapToResponse(wishlist);
    }

    @Transactional
    public void removeFromWishlist(Long studentId, Long courseId) {
        if (!wishlistRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Course not in wishlist");
        }
        wishlistRepository.deleteByStudentIdAndCourseId(studentId, courseId);
    }

    public List<WishlistResponse> getStudentWishlist(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(studentId);
        }
        return wishlistRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public boolean isInWishlist(Long studentId, Long courseId) {
        return wishlistRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .studentId(wishlist.getStudent().getId())
                .courseId(wishlist.getCourseId())
                .addedAt(wishlist.getAddedAt())
                .build();
    }
}
