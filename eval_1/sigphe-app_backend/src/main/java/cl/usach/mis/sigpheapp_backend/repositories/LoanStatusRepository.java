package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.LoanStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanStatusRepository extends JpaRepository<LoanStatusEntity, Long> {
    Optional<LoanStatusEntity> findByName(String name);
}
