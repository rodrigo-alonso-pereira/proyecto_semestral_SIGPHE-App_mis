package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KardexSummaryDTO {
    private LocalDateTime registrationDate;
    private int quantity;
    private String toolName;
    private String kardexTypeName;
    private String workerName;
}
