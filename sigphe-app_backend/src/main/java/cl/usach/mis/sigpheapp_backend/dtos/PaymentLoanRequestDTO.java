package cl.usach.mis.sigpheapp_backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLoanRequestDTO {

    @NotNull(message = "El ID del cliente es requerido")
    private Long customerId;

    @NotNull(message = "El monto del pago es requerido")
    private Double paymentAmount;

}
