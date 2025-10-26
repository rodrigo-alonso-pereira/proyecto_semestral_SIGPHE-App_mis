package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Long id;
    private LocalDateTime startDate, returnDate, dueDate, paymentDate;
    private BigDecimal totalAmount, totalPenalties;
    private String loanStatus, customerName;
}
