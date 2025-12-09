package cl.usach.mis.sigpheapp_backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeRequestDTO {

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDateTime startDate;

    @NotNull(message = "La fecha de fin es requerida")
    private LocalDateTime endDate;
}
