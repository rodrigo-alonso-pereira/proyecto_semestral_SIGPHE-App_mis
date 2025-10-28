package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.LoanDetailEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetailEntity, Long> {
    List<LoanDetailEntity> findAllByLoanIdEquals(@NotNull Long id);
}
