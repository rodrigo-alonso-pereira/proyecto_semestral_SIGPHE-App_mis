package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolDTO {
    private Long id;
    private String name;
    private BigDecimal rentalValue;
    private String category;
    private String status;
    private String model;
}
