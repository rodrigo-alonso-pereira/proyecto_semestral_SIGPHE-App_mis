package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.KardexSummaryDTO;
import cl.usach.mis.sigpheapp_backend.entities.KardexEntity;
import cl.usach.mis.sigpheapp_backend.entities.ToolEntity;
import cl.usach.mis.sigpheapp_backend.exceptions.ResourceNotFoundException;
import cl.usach.mis.sigpheapp_backend.repositories.KardexRepository;
import cl.usach.mis.sigpheapp_backend.repositories.ToolRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class KardexService {

    @Autowired KardexRepository kardexRepository;
    @Autowired ToolRepository toolRepository;

    public List<KardexSummaryDTO> getAllKardexEntries() {
        return kardexRepository.findAll().stream()
                .map(this::toKardexDTO)
                .toList();
    }

    public List<KardexSummaryDTO> getKardexEntriesByToolId(@NotNull Long id) {
        ToolEntity tool = getToolById(id);
        return kardexRepository.findAllByToolIdEqualsOrderByDateTimeDesc(tool.getId()).stream()
                .map(this::toKardexDTO)
                .toList();
    }

    public List<KardexSummaryDTO> getKardexEntriesByDateRange(@NotNull LocalDateTime startDate,
                                                              @NotNull LocalDateTime endDate) {
        return kardexRepository.findAllByDateTimeBetweenOrderByDateTimeDesc(startDate, endDate).stream()
                .map(this::toKardexDTO)
                .toList();
    }

    public List<KardexSummaryDTO> getKardexEntriesByToolIdAndDateRange(@NotNull Long id,
                                                                       @NotNull LocalDateTime startDate,
                                                                       @NotNull LocalDateTime endDate) {
        ToolEntity tool = getToolById(id);
        return kardexRepository.findAllByToolIdEqualsAndDateTimeBetweenOrderByDateTimeDesc(tool.getId(), startDate, endDate).stream()
                .map(this::toKardexDTO)
                .toList();
    }

    /* Metodos auxiliares */
    private ToolEntity getToolById(Long id) {
        return toolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", id));
    }

    /* Mapper Layer */
    private KardexSummaryDTO toKardexDTO(KardexEntity kardex) {
        Objects.requireNonNull(kardex, "KardexEntity cannot be null");
        KardexSummaryDTO dto = new KardexSummaryDTO();
        dto.setRegistrationDate(kardex.getDateTime());
        dto.setQuantity(kardex.getQuantity());
        dto.setToolName(kardex.getTool().getName());
        dto.setKardexTypeName(kardex.getKardexType().getName());
        dto.setWorkerName(kardex.getWorkerUser().getName());
        return dto;
    }
}
