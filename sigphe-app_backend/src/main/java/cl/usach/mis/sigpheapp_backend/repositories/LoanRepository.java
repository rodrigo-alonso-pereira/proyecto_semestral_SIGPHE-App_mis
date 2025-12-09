package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.LoanEntity;
import cl.usach.mis.sigpheapp_backend.entities.LoanStatusEntity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    @Query("SELECT l FROM LoanEntity l " +
            "JOIN FETCH l.loanStatus ls " +
            "JOIN FETCH l.customerUser cu " +
            "ORDER BY l.startDate DESC")
    List<LoanEntity> findAllWithRelations();

    @Query("SELECT l FROM LoanEntity l " +
            "JOIN FETCH l.loanStatus ls " +
            "JOIN FETCH l.customerUser cu " +
            "WHERE ls.name IN :statuses " +
            "ORDER BY l.startDate DESC")
    List<LoanEntity> findByLoanStatusNameInWithRelations(@NotEmpty @NotNull List<String> statuses);

    List<LoanEntity> findAllByCustomerUserIdEquals(@NotNull Long id);

    List<LoanEntity> findAllByLoanStatusNameInAndStartDateBetweenOrderByStartDateDesc(
            @NotEmpty @NotNull List<String> statuses, LocalDateTime localDateTime, LocalDateTime now);

    List<LoanEntity> findAllByLoanStatusAndDueDateBefore(LoanStatusEntity loanStatus, LocalDateTime dueDate);
}