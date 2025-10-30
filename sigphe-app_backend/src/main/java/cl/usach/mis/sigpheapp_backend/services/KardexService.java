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

    /**
     * Obtiene todas las entradas del kardex.
     *
     * @return Lista de KardexSummaryDTO que representan todas las entradas del kardex.
     */
    public List<KardexSummaryDTO> getAllKardexEntries() {
        return kardexRepository.findAllByOrderByDateTimeDesc().stream()
                .map(this::toKardexDTO)
                .toList();
    }

    /**
     * Obtiene las entradas del kardex para una herramienta específica.
     *
     * @param id ID de la herramienta.
     * @return Lista de KardexSummaryDTO que representan las entradas del kardex para la herramienta especificada.
     * @throws ResourceNotFoundException si la herramienta con el ID proporcionado no existe.
     */
    public List<KardexSummaryDTO> getKardexEntriesByToolId(@NotNull Long id) {
        ToolEntity tool = getToolById(id);
        return kardexRepository.findAllByToolIdOrderByDateTimeDesc(tool.getId()).stream()
                .map(this::toKardexDTO)
                .toList();
    }

    /**
     * Obtiene las entradas del kardex dentro de un rango de fechas específico.
     *
     * @param startDate Fecha de inicio del rango.
     * @param endDate Fecha de fin del rango.
     * @return Lista de KardexSummaryDTO que representan las entradas del kardex dentro del rango de fechas especificado.
     */
    public List<KardexSummaryDTO> getKardexEntriesByDateRange(@NotNull LocalDateTime startDate,
                                                              @NotNull LocalDateTime endDate) {
        return kardexRepository.findAllByDateTimeBetweenOrderByDateTimeDesc(startDate, endDate).stream()
                .map(this::toKardexDTO)
                .toList();
    }

    /**
     * Obtiene las entradas del kardex para una herramienta específica dentro de un rango de fechas.
     *
     * @param id ID de la herramienta.
     * @param startDate Fecha de inicio del rango.
     * @param endDate Fecha de fin del rango.
     * @return Lista de KardexSummaryDTO que representan las entradas del kardex para la herramienta especificada dentro del rango de fechas.
     * @throws ResourceNotFoundException si la herramienta con el ID proporcionado no existe.
     */
    public List<KardexSummaryDTO> getKardexEntriesByToolIdAndDateRange(@NotNull Long id,
                                                                       @NotNull LocalDateTime startDate,
                                                                       @NotNull LocalDateTime endDate) {
        ToolEntity tool = getToolById(id);
        return kardexRepository.findAllByToolIdEqualsAndDateTimeBetweenOrderByDateTimeDesc(tool.getId(), startDate, endDate).stream()
                .map(this::toKardexDTO)
                .toList();
    }

    /* Metodos auxiliares */

    /**
     * Obtiene una herramienta por su ID.
     *
     * @param id ID de la herramienta.
     * @return ToolEntity correspondiente al ID proporcionado.
     * @throws ResourceNotFoundException si la herramienta con el ID proporcionado no existe.
     */
    private ToolEntity getToolById(Long id) {
        return toolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", id));
    }

    /* Mapper Layer */

    /**
     * Convierte una entidad KardexEntity a un DTO KardexSummaryDTO.
     *
     * @param kardex Entidad KardexEntity a convertir.
     * @return KardexSummaryDTO correspondiente a la entidad proporcionada.
     */
    private KardexSummaryDTO toKardexDTO(KardexEntity kardex) {
        Objects.requireNonNull(kardex, "KardexEntity cannot be null");
        KardexSummaryDTO dto = new KardexSummaryDTO();
        dto.setRegistrationDate(kardex.getDateTime());
        dto.setQuantity(kardex.getQuantity());
        dto.setToolId(kardex.getTool().getId());
        dto.setToolName(kardex.getTool().getName());
        dto.setKardexTypeName(kardex.getKardexType().getName());
        dto.setWorkerName(kardex.getWorkerUser().getName());
        return dto;
    }
}
