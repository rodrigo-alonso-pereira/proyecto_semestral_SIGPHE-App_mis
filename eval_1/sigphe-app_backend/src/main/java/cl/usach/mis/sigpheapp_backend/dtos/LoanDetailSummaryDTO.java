package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDetailSummaryDTO {
    private ToolSummaryDTO tool;
    private BigDecimal rentalValueAtLoanTime;
}
