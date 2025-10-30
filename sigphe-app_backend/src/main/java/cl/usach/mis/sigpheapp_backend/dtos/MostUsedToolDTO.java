package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MostUsedToolDTO {
    private Long id;
    private String name;
    private String model;
    private String brand;
    private Long usageCount;
}
