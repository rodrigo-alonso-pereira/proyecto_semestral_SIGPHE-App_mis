package cl.usach.mis.sigpheapp_backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnLoanRequestDTO {

    @NotNull(message = "Worker ID is required")
    private Long workerId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Tool conditions are required")
    private Map<String, Long> toolConditions;
}
