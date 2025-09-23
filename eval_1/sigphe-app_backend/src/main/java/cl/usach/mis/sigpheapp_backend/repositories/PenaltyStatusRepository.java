package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.PenaltyStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PenaltyStatusRepository extends JpaRepository<PenaltyStatusEntity, Long> {
    Optional<PenaltyStatusEntity> findByName(String name);
}
