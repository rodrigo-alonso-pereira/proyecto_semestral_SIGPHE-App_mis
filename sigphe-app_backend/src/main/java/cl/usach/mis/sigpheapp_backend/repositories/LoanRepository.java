package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.LoanEntity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findAllById(@NotNull Long id);
    List<LoanEntity> findByLoanStatusNameIn(@NotEmpty @NotNull List<String> statuses);
    List<LoanEntity> findAllByCustomerUserIdEquals(@NotNull Long id);
    List<LoanEntity> findAllByLoanStatusNameInAndStartDateBetweenOrderByStartDateDesc(
            @NotEmpty @NotNull List<String> statuses, LocalDateTime localDateTime, LocalDateTime now);
}
