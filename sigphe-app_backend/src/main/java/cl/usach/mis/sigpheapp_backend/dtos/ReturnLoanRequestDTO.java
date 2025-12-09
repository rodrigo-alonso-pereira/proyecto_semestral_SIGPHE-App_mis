package cl.usach.mis.sigpheapp_backend.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnLoanRequestDTO {

    @NotNull(message = "El ID del trabajador es requerido")
    private Long workerId;

    @NotNull(message = "El ID del cliente es requerido")
    private Long customerId;

    @NotNull(message = "Las condiciones de las herramientas son requeridas")
    @NotEmpty(message = "Las condiciones de las herramientas no pueden estar vacías")
    private Map<Long, String> toolConditions;
}
