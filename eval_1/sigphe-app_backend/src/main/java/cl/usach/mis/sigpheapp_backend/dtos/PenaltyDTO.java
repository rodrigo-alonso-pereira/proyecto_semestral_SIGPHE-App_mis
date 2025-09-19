package cl.usach.mis.sigpheapp_backend.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PenaltyDTO {
    private BigDecimal penaltyAmount;
    private LocalDateTime penaltyDate;
    private String description;
    private Long loanId;
    private PenaltyTypeDTO penaltyType;
    private PenaltyStatusDTO penaltyStatus;
}
