package cl.usach.mis.sigpheapp_backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateToolRequestDTO {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Replacement value is required")
    private BigDecimal replacementValue;

    @NotNull(message = "Rental value is required")
    private BigDecimal rentalValue;

    @NotNull(message = "Tool category ID is required")
    private Long toolCategoryId;

    @NotNull(message = "Model ID is required")
    private Long modelId;

    @NotNull(message = "Tool status ID is required")
    private Long toolStatusId;

    @NotNull(message = "Worker ID is required")
    private Long workerId;
}
