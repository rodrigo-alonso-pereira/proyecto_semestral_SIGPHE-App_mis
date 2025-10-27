package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
public interface KardexRepository extends JpaRepository<KardexEntity, Long> {
    List<KardexEntity> findAllByOrderByDateTimeDesc();
    List<KardexEntity> findAllByToolIdOrderByDateTimeDesc(Long id);
    List<KardexEntity> findAllByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime startDate, LocalDateTime endDate);
    List<KardexEntity> findAllByToolIdEqualsAndDateTimeBetweenOrderByDateTimeDesc(Long id,
                                                                                  LocalDateTime startDate,
                                                                                  LocalDateTime endDate);
}
