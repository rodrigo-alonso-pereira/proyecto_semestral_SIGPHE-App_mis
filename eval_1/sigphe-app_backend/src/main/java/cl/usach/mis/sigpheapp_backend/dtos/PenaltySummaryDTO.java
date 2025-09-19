package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PenaltySummaryDTO {
    private BigDecimal penaltyAmount;
    private LocalDateTime penaltyDate;
    private String description;
    private Long loanId;
    private PenaltyTypeSummaryDTO penaltyType;
    private PenaltyStatusSummaryDTO penaltyStatus;
}
