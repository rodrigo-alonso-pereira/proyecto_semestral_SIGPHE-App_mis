package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.CreateToolRequestDTO;
import cl.usach.mis.sigpheapp_backend.dtos.ToolDTO;
import cl.usach.mis.sigpheapp_backend.entities.*;
import cl.usach.mis.sigpheapp_backend.repositories.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ToolService {

    @Autowired ToolRepository toolRepository;
    @Autowired ToolStatusRepository toolStatusRepository;
    @Autowired ToolCategoryRepository toolCategoryRepository;
    @Autowired ModelRepository modelRepository;
    @Autowired UserRepository userRepository;
    @Autowired KardexRepository kardexRepository;
    @Autowired KardexTypeRepository kardexTypeRepository;

    public List<ToolDTO> getAllTools() {
        return toolRepository.findAll().stream()
                .map(this::toToolDTO)
                .toList();
    }

    public List<ToolDTO> createTool(CreateToolRequestDTO dto) {
        ToolCategoryEntity category = getToolCategoryById(dto.getToolCategoryId()); // Obtiene categoria
        ModelEntity model = getModelById(dto.getModelId()); // Obtiene modelo
        UserEntity worker = getUserById(dto.getWorkerId()); // Obtiene trabajador
        List<ToolDTO> createdTools = new ArrayList<>();

        // Crea tantas herramientas como la cantidad indicada
        for (int i = 0; i < dto.getQuantity(); i++) {
            ToolEntity newTool = new ToolEntity();
            newTool.setName(dto.getName());
            newTool.setReplacementValue(dto.getReplacementValue());
            newTool.setRentalValue(dto.getRentalValue());
            newTool.setToolCategory(category);
            newTool.setModel(model);
            newTool.setToolStatus(getToolStatusByName("Disponible")); // Tool -> Disponible
            ToolEntity savedTool = toolRepository.save(newTool);
            createdTools.add(toToolDTO(savedTool));

            // Agrega entrada al kardex por cada herramienta creada
            addKardexEntry(1, savedTool, getKardexTypeByName("Ingreso"), worker); // Agrega entrada al kardex
        }

        return createdTools;
    }

    /* Metodos auxiliares */
    private ToolStatusEntity getToolStatusByName(String name) {
        return toolStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tool status name: " + name));
    }

    private ToolCategoryEntity getToolCategoryById(Long id) {
        return toolCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tool ID: "+ id));
    }

    private UserEntity getUserById(Long workerId) {
        return userRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid worker ID: " + workerId));
    }

    private ModelEntity getModelById(Long id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid model ID: "+ id));
    }

    private KardexTypeEntity getKardexTypeByName(String name) {
        return kardexTypeRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid kardex type name: " + name));
    }

    private void addKardexEntry(int quantity, ToolEntity tool, KardexTypeEntity type, UserEntity worker) {
        KardexEntity kardexEntry = new KardexEntity();
        kardexEntry.setDateTime(LocalDateTime.now());
        kardexEntry.setQuantity(quantity);
        kardexEntry.setTool(tool);
        kardexEntry.setKardexType(type);
        kardexEntry.setWorkerUser(worker);
        kardexRepository.save(kardexEntry);
    }


    /* Metodos Mapper */

    // ToolEntity -> ToolDTO
    private ToolDTO toToolDTO(ToolEntity entity) {
        Objects.requireNonNull(entity, "ToolEntity cannot be null");
        ToolDTO dto = new ToolDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setRentalValue(entity.getRentalValue());
        dto.setReplacementValue(entity.getReplacementValue());
        dto.setCategory(Optional.ofNullable(entity.getToolCategory())
                .map(ToolCategoryEntity::getName)
                .orElse("Unknown"));
        dto.setStatus(Optional.ofNullable(entity.getToolStatus())
                .map(ToolStatusEntity::getName)
                .orElse("Unknown"));
        dto.setModel(Optional.ofNullable(entity.getModel())
                .map(ModelEntity::getName)
                .orElse("Unknown"));
        return dto;
    }
}
