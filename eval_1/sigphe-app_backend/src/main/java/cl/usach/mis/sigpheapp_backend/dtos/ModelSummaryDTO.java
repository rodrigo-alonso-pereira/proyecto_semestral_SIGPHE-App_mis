package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelSummaryDTO {
    private String name;
    private boolean isActive;
    private BrandSummaryDTO brand;
}
