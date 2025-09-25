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
public class KardexController {

    @Autowired KardexService kardexService;

    @GetMapping
    public ResponseEntity<List<KardexSummaryDTO>> getAll() {
        return ResponseEntity.ok(kardexService.getAllKardexEntries());
    }

    @GetMapping("/tool/{id}/history")
    public ResponseEntity<List<KardexSummaryDTO>> getToolHistory(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(kardexService.getKardexEntriesByToolId(id));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<KardexSummaryDTO>> getAllByDateRange(@Valid @RequestBody DateRangeRequestDTO request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        return ResponseEntity.ok(kardexService.getKardexEntriesByDateRange(request.getStartDate(), request.getEndDate()));
    }

    @GetMapping("/tool/{id}/history/date-range")
    public ResponseEntity<List<KardexSummaryDTO>> getToolHistoryByDateRange(@PathVariable @NotNull Long id,
                                                                            @Valid @RequestBody DateRangeRequestDTO request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        return ResponseEntity.ok(kardexService.getKardexEntriesByToolIdAndDateRange(id, request.getStartDate(), request.getEndDate()));
    }
}
