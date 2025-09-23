package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanSummaryDTO {
    private Long id;
    private LocalDateTime startDate, returnDate;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private BigDecimal totalAmount;
    private BigDecimal totalPenalties;
    private String loanStatus;
    private String customerName;
}
