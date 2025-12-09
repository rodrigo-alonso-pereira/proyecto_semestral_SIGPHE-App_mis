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

    @NotNull(message = "El nombre es requerido")
    private String name;

    @NotNull(message = "El valor de reemplazo es requerido")
    private BigDecimal replacementValue;

    @NotNull(message = "El valor de alquiler es requerido")
    private BigDecimal rentalValue;

    @NotNull(message = "El ID de categoría de herramienta es requerido")
    private Long toolCategoryId;

    @NotNull(message = "El ID del modelo es requerido")
    private Long modelId;

    @NotNull(message = "El ID del estado de herramienta es requerido")
    private Long toolStatusId;

    @NotNull(message = "El ID del trabajador es requerido")
    private Long workerId;
}
