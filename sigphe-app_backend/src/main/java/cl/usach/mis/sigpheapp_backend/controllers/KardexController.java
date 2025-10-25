package cl.usach.mis.sigpheapp_backend.controllers;

import cl.usach.mis.sigpheapp_backend.dtos.DateRangeRequestDTO;
import cl.usach.mis.sigpheapp_backend.dtos.KardexSummaryDTO;
import cl.usach.mis.sigpheapp_backend.services.KardexService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kardex")
@CrossOrigin("*")
public class KardexController {

    @Autowired KardexService kardexService;

    /**
     * Obtiene todas las entradas del kardex.
     *
     * @return ResponseEntity que contiene una lista de KardexSummaryDTO que representan todas las entradas del kardex.
     */
    @GetMapping
    public ResponseEntity<List<KardexSummaryDTO>> getAll() {
        return ResponseEntity.ok(kardexService.getAllKardexEntries());
    }

    /**
     * Obtiene las entradas del kardex para una herramienta específica.
     *
     * @param id ID de la herramienta.
     * @return ResponseEntity que contiene una lista de KardexSummaryDTO que representan las entradas del kardex para la herramienta especificada.
     */
    @GetMapping("/tool/{id}/history")
    public ResponseEntity<List<KardexSummaryDTO>> getToolHistory(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(kardexService.getKardexEntriesByToolId(id));
    }

    /**
     * Obtiene las entradas del kardex dentro de un rango de fechas específico.
     *
     * @param request DateRangeRequestDTO que contiene la fecha de inicio y la fecha de fin del rango.
     * @return ResponseEntity que contiene una lista de KardexSummaryDTO que representan las entradas del kardex dentro del rango de fechas especificado.
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<KardexSummaryDTO>> getAllByDateRange(@Valid @RequestBody DateRangeRequestDTO request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        return ResponseEntity.ok(kardexService.getKardexEntriesByDateRange(request.getStartDate(), request.getEndDate()));
    }

    /**
     * Obtiene las entradas del kardex para una herramienta específica dentro de un rango de fechas.
     *
     * @param id ID de la herramienta.
     * @param request DateRangeRequestDTO que contiene la fecha de inicio y la fecha de fin del rango.
     * @return ResponseEntity que contiene una lista de KardexSummaryDTO que representan las entradas del kardex para la herramienta especificada dentro del rango de fechas.
     */
    @GetMapping("/tool/{id}/history/date-range")
    public ResponseEntity<List<KardexSummaryDTO>> getToolHistoryByDateRange(@PathVariable @NotNull Long id,
                                                                            @Valid @RequestBody DateRangeRequestDTO request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        return ResponseEntity.ok(kardexService.getKardexEntriesByToolIdAndDateRange(id, request.getStartDate(), request.getEndDate()));
    }
}
