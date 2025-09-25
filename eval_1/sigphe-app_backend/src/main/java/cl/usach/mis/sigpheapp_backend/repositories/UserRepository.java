package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.UserEntity;
import cl.usach.mis.sigpheapp_backend.repositories.projection.ClientsWithDebtsProjection;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByName(@NotNull String name);
    List<UserEntity> findAllByUserStatusIdEquals(@NotNull Long userStatusId);

    @Query(value = "select " +
            "   u.name as userName, " +
            "   u.email as userEmail, " +
            "   us.name as userStatus, " +
            "   ut.name as userType, " +
            "   l.id as loanId, " +
            "   ls.name as statusLoan, " +
            "   l.due_date as dueDate " +
            "from sigphe.users u " +
            "join sigphe.loans l on u.id = l.customer_user_id " +
            "join sigphe.loan_statuses ls on ls.id = l.loan_status_id " +
            "join sigphe.user_statuses us on us.id = u.user_status_id " +
            "join sigphe.user_types ut on ut.id = u.user_type_id " +
            "where (l.due_date between :startDate and :endDate) " +
            "and (ls.name = :statusLoanOverdue or ls.name = :statusLoanActive) " +
            "order by l.due_date desc " +
            "limit 3",
            nativeQuery = true)
    List<ClientsWithDebtsProjection> findAllUserWithDebtsBetweenDates(@NotNull LocalDateTime startDate,
                                                                      @NotNull LocalDateTime endDate,
                                                                      String statusLoanOverdue,
                                                                      String statusLoanActive);
}
