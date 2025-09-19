package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolDTO {
    private String name;
    private BigDecimal rentalValue;
    private ToolCategoryDTO category;
    private ToolStatusDTO status;
    private ModelDTO model;
}
