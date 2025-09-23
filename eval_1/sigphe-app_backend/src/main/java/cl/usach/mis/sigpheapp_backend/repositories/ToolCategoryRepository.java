package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.ToolCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolCategoryRepository extends JpaRepository<ToolCategoryEntity, Long> {
}
