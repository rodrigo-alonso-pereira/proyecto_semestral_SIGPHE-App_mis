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

    /**
     * Obtiene todas las herramientas.
     *
     * @return Lista de herramientas.
     */
    @GetMapping
    public ResponseEntity<List<ToolDTO>> getAll() {
        List<ToolDTO> tools = toolService.getAllTools();
        return ResponseEntity.ok(tools);
    }

    /**
     * Obtiene todas las herramientas activas.
     *
     * @return Lista de herramientas activas.
     */
    @GetMapping("/active")
    public ResponseEntity<List<ToolDTO>> getActiveTools() {
        List<ToolDTO> tools = toolService.getActiveTools();
        return ResponseEntity.ok(tools);
    }

    /**
     * Obtiene los estados de las herramientas.
     *
     * @return Lista de estados de herramientas.
     */
    @GetMapping("/status")
    public ResponseEntity<List<ToolStatusDTO>> getToolStatuses() {
        List<ToolStatusDTO> toolStatuses = toolService.getToolStatuses();
        return ResponseEntity.ok(toolStatuses);
    }

    /**
     * Obtiene las categorías de las herramientas.
     *
     * @return Lista de categorías de herramientas.
     */
    @GetMapping("/category")
    public ResponseEntity<List<ToolCategoriesDTO>> getToolCategories() {
        List<ToolCategoriesDTO> toolCategories = toolService.getToolCategories();
        return ResponseEntity.ok(toolCategories);
    }

    /**
     * Obtiene los modelos de las herramientas.
     *
     * @return Lista de modelos de herramientas.
     */
    @GetMapping("/model")
    public ResponseEntity<List<ToolModelsDTO>> getToolModels() {
        List<ToolModelsDTO> toolModels = toolService.getToolModels();
        return ResponseEntity.ok(toolModels);
    }

    /**
     * Obtiene una herramienta por su ID.
     *
     * @param id ID de la herramienta.
     * @return Herramienta encontrada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ToolDTO> getToolById(@PathVariable @NotNull Long id) {
        ToolDTO tool = toolService.getToolById(id);
        return ResponseEntity.ok(tool);
    }

    /**
     * Crea una nueva herramienta.
     *
     * @param request Datos de la herramienta a crear.
     * @return Herramienta creada.
     */
    @PostMapping
    public ResponseEntity<List<ToolDTO>> createTool(@Valid @RequestBody CreateToolRequestDTO request) {
        List<ToolDTO> createdTools = toolService.createTool(request);
        return ResponseEntity.created(URI.create("/api/v1/tools")).body(createdTools);
    }

    @PutMapping("/update")
    public ResponseEntity<ToolDTO> updateTool(@Valid @RequestBody UpdateToolRequestDTO request) {
        ToolDTO updatedTool = toolService.updateTool(request);
        return ResponseEntity.ok(updatedTool);
    }

    /**
     * Actualiza una herramienta existente.
     *
     * @param id ID de la herramienta a actualizar.
     * @param request Datos actualizados de la herramienta.
     * @return Herramienta actualizada.
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ToolDTO> deactivateTool(@PathVariable @NotNull Long id,
                                                  @Valid @RequestBody DeactivateToolRequestDTO request) {
        ToolDTO updatedTool = toolService.deactivateTool(id, request);
        return ResponseEntity.ok(updatedTool);
    }

    /**
     * Obtiene las herramientas más prestadas.
     *
     * @return Lista de herramientas más prestadas.
     */
    @GetMapping("/most-borrowed")
    public ResponseEntity<List<MostUsedToolDTO>> getMostBorrowedTools() {
        List<MostUsedToolDTO> tools = toolService.getMostUsedTools();
        return ResponseEntity.ok(tools);
    }

    /**
     * Obtiene las herramientas más prestadas en un rango de fechas.
     *
     * @param request Rango de fechas.
     * @return Lista de herramientas más prestadas en el rango de fechas.
     */
    @GetMapping("/most-borrowed/date-range")
    public ResponseEntity<List<MostUsedToolDTO>> getMostBorrowedTools(
            @Valid @RequestBody DateRangeRequestDTO request) {
        List<MostUsedToolDTO> tools = toolService.getMostUsedToolsByDateRange(request.getStartDate(),
                request.getEndDate());
        return ResponseEntity.ok(tools);
    }
}
