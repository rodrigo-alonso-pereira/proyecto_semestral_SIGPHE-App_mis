package cl.usach.mis.sigpheapp_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "loan_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDetailEntity {

    @EmbeddedId
    private LoanDetailEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("toolId")
    @JoinColumn(name = "tool_id", nullable = false)
    private ToolEntity tool;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("loanId")
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanEntity loan;

    @Column(name = "rental_value_at_time", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalValueAtTime;
}

