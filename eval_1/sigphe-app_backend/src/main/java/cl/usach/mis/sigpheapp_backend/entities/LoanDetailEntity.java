package cl.usach.mis.sigpheapp_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "loan_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDetailEntity {
    @Id
    @Column(nullable = false)
    private ToolEntity tool;

    @Id
    @Column(nullable = false)
    private LoanEntity loan;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalValueAtTime;
}

