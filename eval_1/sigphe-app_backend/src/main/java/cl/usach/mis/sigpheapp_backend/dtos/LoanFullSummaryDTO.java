package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanFullSummaryDTO {

    private Long id;
    private LocalDateTime startDate, returnDate;
    private LocalDateTime dueDate;
    private BigDecimal totalAmount;
    private String loanStatus;
    private String customerName;
    private List<PenaltyDTO> penalties;
    private List<LoanDetailDTO> loanDetails;
}
