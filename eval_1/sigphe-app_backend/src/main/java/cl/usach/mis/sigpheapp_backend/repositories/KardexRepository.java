package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KardexRepository extends JpaRepository<KardexEntity, Long> {
}
