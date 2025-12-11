package e_learning.admin_service.services;

import e_learning.admin_service.clients.AuthClient;
import e_learning.admin_service.entities.AdminLog;
import e_learning.admin_service.repositories.AdminLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminActionService {

    private final AdminLogRepository adminLogRepository;
    private final AuthClient authClient;

    public AdminActionService(AdminLogRepository adminLogRepository, AuthClient authClient) {
        this.adminLogRepository = adminLogRepository;
        this.authClient = authClient;
    }

    @Transactional
    public void lockUser(Long adminId, Long targetUserId, boolean locked) {
        // Call Auth Service
        authClient.lockUser(targetUserId, locked);

        // Log action
        AdminLog log = AdminLog.builder()
                .adminId(adminId)
                .action(locked ? "LOCK_USER" : "UNLOCK_USER")
                .targetType("USER")
                .targetId(targetUserId.toString())
                .details("User account triggered lock/unlock")
                .timestamp(LocalDateTime.now())
                .build();

        adminLogRepository.save(log);
    }

    public List<AdminLog> getLogs() {
        return adminLogRepository.findAllByOrderByTimestampDesc();
    }
}
