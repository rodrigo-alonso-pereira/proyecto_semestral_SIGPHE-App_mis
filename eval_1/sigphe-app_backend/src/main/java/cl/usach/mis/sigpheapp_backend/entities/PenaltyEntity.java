package cl.usach.mis.sigpheapp_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "penalties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(nullable = false)
    private LocalDateTime penaltyDate;

    @Column(length = 500)
    private String description;

    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private LoanEntity loan;

    @Column(nullable = false)
    private PenaltyTypeEntity penaltyType;

    @Column(nullable = false)
    private PenaltyStatusEntity penaltyStatus;
}

