package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolSummaryDTO {
    private String name;
    private BigDecimal rentalValue;
    private ToolCategorySummaryDTO category;
    private ToolStatusSummaryDTO status;
    private ModelSummaryDTO model;
}
