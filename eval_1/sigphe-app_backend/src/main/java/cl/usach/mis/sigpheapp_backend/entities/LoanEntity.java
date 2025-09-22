package cl.usach.mis.sigpheapp_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LoanEntity {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "total_rental", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRental;

    @Column(name = "total_penalties", precision = 10, scale = 2)
    private BigDecimal totalPenalties = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_status_id", nullable = false)
    private LoanStatusEntity loanStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_user_id", nullable = false)
    private UserEntity customerUser;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LoanDetailEntity> loanDetails = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PenaltyEntity> penalties;

    // Metodo helper para agregar un LoanDetail y establecer la relacion bidireccional
    public void addLoanDetail(LoanDetailEntity loanDetail) {
        this.loanDetails.add(loanDetail);
        loanDetail.setLoan(this);  // Establecer relación bidireccional
    }

    // Metodo helper para agregar una Penalty y establecer la relacion bidireccional
    public void addPenalty(PenaltyEntity penalty) {
        if (this.penalties == null) {
            this.penalties = new java.util.ArrayList<>();
        }
        this.penalties.add(penalty);
        penalty.setLoan(this);
    }
}
