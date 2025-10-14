package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.ToolStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolStatusRepository extends JpaRepository<ToolStatusEntity, Long> {
    Optional<ToolStatusEntity> findByName(String prestada);
}
