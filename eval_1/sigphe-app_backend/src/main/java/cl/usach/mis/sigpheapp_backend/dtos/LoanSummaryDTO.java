package cl.usach.mis.sigpheapp_backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanSummaryDTO {
    private Long id;
    private LocalDateTime startDate, returnDate;
    private LocalDateTime dueDate;
    private BigDecimal totalAmount;
    private String loanStatus;
    private String customerName;
}
