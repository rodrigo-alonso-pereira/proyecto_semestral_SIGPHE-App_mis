package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.PenaltyTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PenaltyTypeRepository extends JpaRepository<PenaltyTypeEntity, Long> {
    Optional<PenaltyTypeEntity> findByName(String name);
}
