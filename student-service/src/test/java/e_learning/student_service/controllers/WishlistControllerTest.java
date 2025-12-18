package e_learning.student_service.controllers;

import e_learning.student_service.dto.WishlistResponse;
import e_learning.student_service.services.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    private WishlistResponse wishlistResponse;

    @BeforeEach
    void setUp() {
        wishlistResponse = WishlistResponse.builder()
                .id(1L)
                .studentId(1L)
                .courseId(1L)
                .addedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /wishlist/{studentId}/courses/{courseId} - should add to wishlist")
    void addToWishlist_ShouldReturnCreatedWishlist() throws Exception {
        when(wishlistService.addToWishlist(1L, 1L)).thenReturn(wishlistResponse);

        mockMvc.perform(post("/wishlist/1/courses/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.courseId").value(1));

        verify(wishlistService, times(1)).addToWishlist(1L, 1L);
    }

    @Test
    @DisplayName("DELETE /wishlist/{studentId}/courses/{courseId} - should remove from wishlist")
    void removeFromWishlist_ShouldReturnNoContent() throws Exception {
        doNothing().when(wishlistService).removeFromWishlist(1L, 1L);

        mockMvc.perform(delete("/wishlist/1/courses/1"))
                .andExpect(status().isNoContent());

        verify(wishlistService, times(1)).removeFromWishlist(1L, 1L);
    }

    @Test
    @DisplayName("GET /wishlist/{studentId} - should get student wishlist")
    void getStudentWishlist_ShouldReturnWishlist() throws Exception {
        List<WishlistResponse> wishlist = Arrays.asList(wishlistResponse);
        when(wishlistService.getStudentWishlist(1L)).thenReturn(wishlist);

        mockMvc.perform(get("/wishlist/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].studentId").value(1));

        verify(wishlistService, times(1)).getStudentWishlist(1L);
    }

    @Test
    @DisplayName("GET /wishlist/{studentId}/courses/{courseId}/check - should check if in wishlist")
    void isInWishlist_ShouldReturnBoolean() throws Exception {
        when(wishlistService.isInWishlist(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/wishlist/1/courses/1/check"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(wishlistService, times(1)).isInWishlist(1L, 1L);
    }
}
