package e_learning.catalog_service.repositories;

import e_learning.catalog_service.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {
}
