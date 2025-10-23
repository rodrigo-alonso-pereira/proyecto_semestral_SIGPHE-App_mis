package cl.usach.mis.sigpheapp_backend.controllers;

import cl.usach.mis.sigpheapp_backend.dtos.*;
import cl.usach.mis.sigpheapp_backend.repositories.projection.MostUsedToolProjection;
import cl.usach.mis.sigpheapp_backend.services.ToolService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tools")
@CrossOrigin("*")
public class ToolController {

    @Autowired ToolService toolService;

    // TODO: Solucionar problema N+1
    @GetMapping
    public ResponseEntity<List<ToolDTO>> getAll() {
        List<ToolDTO> tools = toolService.getAllTools();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ToolDTO>> getActiveTools() {
        List<ToolDTO> tools = toolService.getActiveTools();
        return ResponseEntity.ok(tools);
    }

    @PostMapping
    public ResponseEntity<List<ToolDTO>> createTool(@Valid @RequestBody CreateToolRequestDTO request) {
        List<ToolDTO> createdTools = toolService.createTool(request);
        return ResponseEntity.created(URI.create("/api/v1/tools")).body(createdTools);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ToolDTO> deactivateTool(@PathVariable @NotNull Long id,
                                                  @Valid @RequestBody DeactivateToolRequestDTO request) {
        ToolDTO updatedTool = toolService.deactivateTool(id, request);
        return ResponseEntity.ok(updatedTool);
    }

    @GetMapping("/most-borrowed")
    public ResponseEntity<List<MostUsedToolDTO>> getMostBorrowedTools() {
        List<MostUsedToolDTO> tools = toolService.getMostUsedTools();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("/most-borrowed/date-range")
    public ResponseEntity<List<MostUsedToolProjection>> getMostBorrowedTools(
            @Valid @RequestBody DateRangeRequestDTO request) {
        List<MostUsedToolProjection> tools = toolService.getMostUsedToolsByDateRange(request.getStartDate(),
                request.getEndDate());
        return ResponseEntity.ok(tools);
    }
}
