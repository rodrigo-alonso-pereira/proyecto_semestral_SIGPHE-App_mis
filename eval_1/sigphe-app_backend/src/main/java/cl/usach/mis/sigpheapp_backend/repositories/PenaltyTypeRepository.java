package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.PenaltyTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PenaltyTypeRepository extends JpaRepository<PenaltyTypeEntity, Long> {
    Optional<PenaltyTypeEntity> findByName(String name);
}
