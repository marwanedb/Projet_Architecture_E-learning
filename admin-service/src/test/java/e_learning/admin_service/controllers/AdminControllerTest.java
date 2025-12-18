package e_learning.admin_service.controllers;

import e_learning.admin_service.dto.DashboardStatsResponse;
import e_learning.admin_service.services.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    private DashboardStatsResponse dashboardStats;

    @BeforeEach
    void setUp() {
        dashboardStats = DashboardStatsResponse.builder()
                .totalStudents(100L)
                .totalProfessors(10L)
                .totalCourses(50L)
                .totalEnrollments(500L)
                .activeEnrollments(300L)
                .completedEnrollments(150L)
                .build();
    }

    @Test
    @DisplayName("GET /admin/dashboard/stats - should get dashboard statistics")
    void getDashboardStats_ShouldReturnStats() throws Exception {
        when(adminService.getDashboardStats()).thenReturn(dashboardStats);

        mockMvc.perform(get("/admin/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents").value(100))
                .andExpect(jsonPath("$.totalProfessors").value(10))
                .andExpect(jsonPath("$.totalCourses").value(50))
                .andExpect(jsonPath("$.totalEnrollments").value(500));

        verify(adminService, times(1)).getDashboardStats();
    }

    @Test
    @DisplayName("PUT /admin/users/{id}/lock?locked=true - should lock user")
    void lockUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(adminService).lockUser(1L, 1L, true);

        mockMvc.perform(put("/admin/users/1/lock")
                .param("locked", "true")
                .header("X-Admin-Id", "1"))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).lockUser(1L, 1L, true);
    }

    @Test
    @DisplayName("PUT /admin/users/{id}/lock?locked=false - should unlock user")
    void unlockUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(adminService).lockUser(1L, 1L, false);

        mockMvc.perform(put("/admin/users/1/lock")
                .param("locked", "false")
                .header("X-Admin-Id", "1"))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).lockUser(1L, 1L, false);
    }
}
