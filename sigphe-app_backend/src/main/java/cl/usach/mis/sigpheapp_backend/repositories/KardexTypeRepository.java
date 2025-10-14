package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.KardexTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KardexTypeRepository extends JpaRepository<KardexTypeEntity, Long> {
    Optional<KardexTypeEntity> findByName(String name);
}
