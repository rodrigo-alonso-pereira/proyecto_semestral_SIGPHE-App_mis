package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findAllById(Long id);
    List<LoanEntity> findByLoanStatusNameIn(List<String> statuses);
    List<LoanEntity> findAllByCustomerUserIdEquals(Long id);
}
