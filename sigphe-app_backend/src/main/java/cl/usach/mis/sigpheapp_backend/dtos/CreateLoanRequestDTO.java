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

    @Future(message = "La fecha de vencimiento debe ser en el futuro")
    @NotNull(message = "La fecha de vencimiento es requerida")
    private LocalDateTime dueDate;

    @NotNull(message = "El ID del cliente es requerido")
    private Long customerId;

    @NotNull(message = "El ID del trabajador es requerido")
    private Long workerId;

    @NotNull(message = "Los IDs de herramientas no pueden ser nulos")
    @NotEmpty(message = "Se requiere al menos un ID de herramienta")
    private List<Long> toolIds;

}
