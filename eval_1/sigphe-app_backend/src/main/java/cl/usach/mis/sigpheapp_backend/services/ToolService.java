package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.entities.ToolEntity;
import cl.usach.mis.sigpheapp_backend.repositories.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ToolService {

    @Autowired ToolRepository toolRepository;

    public BigDecimal getTotalAmount(List<Long> toolIds) {
        BigDecimal total = new BigDecimal(0);
        // Recorre cada ID de herramienta y suma su valor de alquiler al total
        for (Long toolId : toolIds) {
            total = total.add(toolRepository.findById(toolId)
                    .map(ToolEntity::getRentalValue).orElse(BigDecimal.ZERO));
        }
        return total;
    }

    public boolean isToolAvailable(Long toolId) {
        Optional<ToolEntity> tool = toolRepository.findById(toolId);
        return tool.map(toolEntity -> toolEntity.getToolStatus().getName().equals("Disponible"))
                .orElse(false);
    }
}
