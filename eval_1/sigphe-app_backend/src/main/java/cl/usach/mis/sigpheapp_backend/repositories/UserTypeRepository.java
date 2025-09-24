package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTypeRepository extends JpaRepository<UserTypeEntity, Long> {
    Optional<UserTypeEntity> findByName(String name);
}
