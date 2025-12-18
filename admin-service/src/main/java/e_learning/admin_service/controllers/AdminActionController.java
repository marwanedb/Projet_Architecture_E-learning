package e_learning.admin_service.controllers;

import e_learning.admin_service.entities.AdminLog;
import e_learning.admin_service.services.AdminActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Actions", description = "Administrative actions")
public class AdminActionController {

    private final AdminActionService adminActionService;

    public AdminActionController(AdminActionService adminActionService) {
        this.adminActionService = adminActionService;
    }

    @PutMapping("/users/{userId}/lock")
    @Operation(summary = "Lock/Unlock a user account")
    public ResponseEntity<Void> lockUser(
            @RequestHeader(value = "X-User-Id", required = false) Long adminId,
            @PathVariable Long userId,
            @RequestParam boolean locked) {

        // If header missing, default to 1 for demo
        if (adminId == null)
            adminId = 1L;

        adminActionService.lockUser(adminId, userId, locked);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logs")
    @Operation(summary = "View audit logs")
    public ResponseEntity<List<AdminLog>> getLogs() {
        return ResponseEntity.ok(adminActionService.getLogs());
    }
}
