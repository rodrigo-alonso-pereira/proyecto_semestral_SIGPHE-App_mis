package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MostUsedToolDTO {
    private Long toolId;
    private String toolName;
    private String toolModel;
    private String toolBrand;
    private Long usageCount;
}
