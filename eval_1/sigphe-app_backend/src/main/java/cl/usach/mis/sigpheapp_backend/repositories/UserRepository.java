package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.UserEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByName(String name);
    List<UserEntity> findAllByUserStatusIdEquals(Long userStatusId);
}
