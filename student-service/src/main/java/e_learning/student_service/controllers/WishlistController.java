package e_learning.student_service.controllers;

import e_learning.student_service.dto.WishlistResponse;
import e_learning.student_service.services.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@Tag(name = "Wishlist", description = "Course wishlist/bookmarks management")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    @Operation(summary = "Add a course to student's wishlist")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistService.addToWishlist(studentId, courseId));
    }

    @DeleteMapping("/{studentId}/courses/{courseId}")
    @Operation(summary = "Remove a course from student's wishlist")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        wishlistService.removeFromWishlist(studentId, courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Get all courses in student's wishlist")
    public ResponseEntity<List<WishlistResponse>> getStudentWishlist(@PathVariable Long studentId) {
        return ResponseEntity.ok(wishlistService.getStudentWishlist(studentId));
    }

    @GetMapping("/{studentId}/courses/{courseId}/check")
    @Operation(summary = "Check if a course is in student's wishlist")
    public ResponseEntity<Boolean> isInWishlist(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(wishlistService.isInWishlist(studentId, courseId));
    }
}
