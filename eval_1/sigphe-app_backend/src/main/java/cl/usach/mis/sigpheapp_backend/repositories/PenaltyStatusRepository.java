package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.PenaltyStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PenaltyStatusRepository extends JpaRepository<PenaltyStatusEntity, Long> {
    Optional<PenaltyStatusEntity> findByName(String name);
}
