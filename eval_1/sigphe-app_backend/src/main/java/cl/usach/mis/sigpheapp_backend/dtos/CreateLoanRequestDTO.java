package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoanRequestDTO {

    @Future(message = "Due date must be in the future")
    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Worker ID is required")
    private Long workerId;

    @NotEmpty(message = "At least one tool ID is required")
    private List<Long> toolIds;

}
