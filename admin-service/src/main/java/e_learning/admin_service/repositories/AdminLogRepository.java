package e_learning.admin_service.repositories;

import e_learning.admin_service.entities.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
    List<AdminLog> findByAdminId(Long adminId);

    List<AdminLog> findAllByOrderByTimestampDesc();
}
