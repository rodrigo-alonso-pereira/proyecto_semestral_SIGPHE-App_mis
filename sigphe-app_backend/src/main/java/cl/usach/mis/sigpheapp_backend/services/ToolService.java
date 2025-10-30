package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.*;
import cl.usach.mis.sigpheapp_backend.entities.*;
import cl.usach.mis.sigpheapp_backend.exceptions.BusinessException;
import cl.usach.mis.sigpheapp_backend.exceptions.ResourceNotFoundException;
import cl.usach.mis.sigpheapp_backend.repositories.*;
import cl.usach.mis.sigpheapp_backend.repositories.projection.MostUsedToolProjection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ToolService {

    // Constantes de estados de herramientas
    private static final String STATUS_TOOL_AVAILABLE = "Disponible";
    private static final String STATUS_TOOL_LOANED = "Prestada";
    private static final String STATUS_TOOL_IN_REPAIR = "En Reparacion";
    private static final String STATUS_TOOL_DECOMMISSIONED = "Dada de baja";

    // Constantes de tipos de kardex
    private static final String TYPE_KARDEX_ENTRY = "Ingreso";
    private static final String TYPE_KARDEX_LOAN = "Prestamo";
    private static final String TYPE_KARDEX_RETURN = "Devolucion";
    private static final String TYPE_KARDEX_REPAIR = "Reparacion";
    private static final String TYPE_KARDEX_DECOMMISSION = "Baja";

    @Autowired ToolRepository toolRepository;
    @Autowired ToolStatusRepository toolStatusRepository;
    @Autowired ToolCategoryRepository toolCategoryRepository;
    @Autowired ModelRepository modelRepository;
    @Autowired UserRepository userRepository;
    @Autowired KardexRepository kardexRepository;
    @Autowired KardexTypeRepository kardexTypeRepository;

    // TODO: Solucionar problema N+1

    /**
     * Obtiene todas las herramientas
     *
     * @return Lista de ToolDTO
     */
    public List<ToolDTO> getAllTools() {
        return toolRepository.findAllByOrderByIdDesc().stream()
                .map(this::toToolDTO)
                .toList();
    }

    // TODO: Cambiar logica para obtener herramientas segun un estado solicitado
    /**
     * Obtiene todas las herramientas activas (Disponibles)
     *
     * @return Lista de ToolDTO
     */
    public List<ToolDTO> getActiveTools() {
        return toolRepository.findAllByToolStatusIdEquals(getToolStatusByName(STATUS_TOOL_AVAILABLE).getId()).stream()
                .map(this::toToolDTO)
                .toList();
    }

    /**
     * Obtiene todos los estados de herramientas
     *
     * @return Lista de ToolStatusDTO
     */
    public List<ToolStatusDTO> getToolStatuses() {
        return toolStatusRepository.findAll().stream()
                .map(this::toToolStatusDTO)
                .toList();
    }

    /**
     * Obtiene todas las categorias de herramientas
     *
     * @return Lista de ToolCategoriesDTO
     */
    public List<ToolCategoriesDTO> getToolCategories() {
        return toolCategoryRepository.findAll().stream()
                .map(this::toToolCategoryDTO)
                .toList();
    }

    /**
     * Obtiene todos los modelos de herramientas
     *
     * @return Lista de ToolModelsDTO
     */
    public List<ToolModelsDTO> getToolModels() {
        return modelRepository.findAll().stream()
                .map(this::toModelDTO)
                .toList();
    }

    /**
     * Obtiene una herramienta por su ID
     *
     * @param id ID de la herramienta
     * @return ToolDTO
     */
    public ToolDTO getToolById(@NotNull Long id) {
        ToolEntity tool = getToolEntityById(id);
        return toToolDTO(tool);
    }

    /**
     * Crea nuevas herramientas y agrega entradas al kardex
     *
     * @param dto Datos para crear herramientas
     * @return Lista de ToolDTO creadas
     */
    @Transactional
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
            newTool.setToolStatus(getToolStatusByName(STATUS_TOOL_AVAILABLE)); // Tool -> Disponible
            ToolEntity savedTool = toolRepository.save(newTool);
            createdTools.add(toToolDTO(savedTool));

            // Agrega entrada al kardex por cada herramienta creada
            addKardexEntry(1, savedTool, getKardexTypeByName(TYPE_KARDEX_ENTRY), worker);
        }

        return createdTools;
    }

    /**
     * Actualiza una herramienta existente y agrega entradas al kardex si es necesario
     *
     * @param dto Datos para actualizar la herramienta
     * @return ToolDTO actualizada
     */
    public ToolDTO updateTool(@NotNull Long id, @Valid UpdateToolRequestDTO dto) {
        ToolEntity tool = getToolEntityById(id);
        ToolCategoryEntity category = getToolCategoryById(dto.getToolCategoryId());
        ModelEntity model = getModelById(dto.getModelId());
        UserEntity worker = getUserById(dto.getWorkerId());
        ToolStatusEntity status = getToolStatusById(dto.getToolStatusId());
        if (tool.getToolStatus().getName().equals(STATUS_TOOL_LOANED) ||
                tool.getToolStatus().getName().equals(STATUS_TOOL_DECOMMISSIONED)) {
            throw new BusinessException("Tool status cannot be changed when it is 'Prestada' or 'Dada de baja'.");
        } else if (status.getName().equals(STATUS_TOOL_LOANED) ||
                status.getName().equals(STATUS_TOOL_DECOMMISSIONED)) {
            throw new BusinessException("Tool status cannot be set to 'Prestada' or 'Dada de baja' via update.");
        }

        tool.setName(dto.getName());
        tool.setReplacementValue(dto.getReplacementValue());
        tool.setRentalValue(dto.getRentalValue());
        tool.setToolCategory(category);
        tool.setModel(model);
        tool.setToolStatus(status);
        ToolEntity updatedTool = toolRepository.save(tool);

        if (status.getName().equals(STATUS_TOOL_AVAILABLE)) {
            addKardexEntry(1, updatedTool, getKardexTypeByName(TYPE_KARDEX_ENTRY), worker);
        } else if (status.getName().equals(STATUS_TOOL_IN_REPAIR)) {
            addKardexEntry(-1, updatedTool, getKardexTypeByName(TYPE_KARDEX_REPAIR), worker);
        }

        return toToolDTO(updatedTool);
    }

    /**
     * Desactiva una herramienta y agrega una salida al kardex
     *
     * @param toolId ID de la herramienta a desactivar
     * @param dto Datos para desactivar la herramienta
     * @return ToolDTO desactivada
     */
    @Transactional
    public ToolDTO deactivateTool(@NotNull Long toolId, DeactivateToolRequestDTO dto) {
        ToolEntity tool = getToolEntityById(toolId);
        // TODO: Validar si el usuario es perfil trabajador
        UserEntity worker = getUserById(dto.getWorkerId()); // Obtiene trabajador

        isToolAvailableForDeactivation(tool); // Verifica si la herramienta puede ser desactivada

        tool.setToolStatus(getToolStatusByName(STATUS_TOOL_DECOMMISSIONED)); // tool -> Dada de baja
        ToolEntity updatedTool = toolRepository.save(tool);
        // Agrega salida al kardex
        addKardexEntry(-1, tool, getKardexTypeByName(TYPE_KARDEX_DECOMMISSION), worker);
        return toToolDTO(updatedTool);
    }


    /**
     * Obtiene las herramientas más usadas
     *
     * @return Lista de MostUsedToolDTO
     */
    public List<MostUsedToolDTO> getMostUsedTools() {
        List<MostUsedToolDTO> mostUsedTools = new ArrayList<>();
        for (MostUsedToolProjection projection : toolRepository.findMostUsedTools()) {
            mostUsedTools.add(toMostUsedToolDTO(projection));
        }
        return mostUsedTools;
    }

    /**
     * Obtiene las herramientas más usadas en un rango de fechas
     *
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de MostUsedToolDTO
     */
    public List<MostUsedToolDTO> getMostUsedToolsByDateRange(@NotNull LocalDateTime startDate,
                                                             @NotNull LocalDateTime endDate) {
        List<MostUsedToolDTO> mostUsedTools = new ArrayList<>();
        for (MostUsedToolProjection projection : toolRepository.findMostUsedToolsBetweenDates(startDate, endDate)) {
            mostUsedTools.add(toMostUsedToolDTO(projection));
        }
        return mostUsedTools;
    }

    /* Metodos auxiliares */

    /**
     * Obtiene una herramienta por su ID
     *
     * @param id ID de la herramienta
     * @return ToolEntity
     * @throws ResourceNotFoundException si la herramienta no existe
     */
    private ToolEntity getToolEntityById(Long id) {
        return toolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", id));
    }

    /**
     * Obtiene un estado de herramienta por su nombre
     *
     * @param name Nombre del estado
     * @return ToolStatusEntity
     * @throws ResourceNotFoundException si el estado no existe
     */
    private ToolStatusEntity getToolStatusByName(String name) {
        return toolStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tool Status", "name", name));
    }

    private ToolStatusEntity getToolStatusById(Long id) {
        return toolStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool Status", "id", id));
    }

    /**
     * Obtiene una categoria de herramienta por su ID
     *
     * @param id ID de la categoria
     * @return ToolCategoryEntity
     * @throws ResourceNotFoundException si la categoria no existe
     */
    private ToolCategoryEntity getToolCategoryById(Long id) {
        return toolCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tool Category", "id", id));
    }

    /**
     * Obtiene un usuario por su ID
     *
     * @param workerId ID del usuario
     * @return UserEntity
     * @throws ResourceNotFoundException si el usuario no existe
     */
    private UserEntity getUserById(Long workerId) {
        return userRepository.findById(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", workerId));
    }

    /**
     * Obtiene un modelo por su ID
     *
     * @param id ID del modelo
     * @return ModelEntity
     * @throws ResourceNotFoundException si el modelo no existe
     */
    private ModelEntity getModelById(Long id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Model", "id", id));
    }

    /**
     * Obtiene un tipo de kardex por su nombre
     *
     * @param name Nombre del tipo de kardex
     * @return KardexTypeEntity
     * @throws ResourceNotFoundException si el tipo de kardex no existe
     */
    private KardexTypeEntity getKardexTypeByName(String name) {
        return kardexTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Kardex Type", "name", name));
    }

    /**
     * Agrega una entrada al kardex
     *
     * @param quantity Cantidad (positiva o negativa)
     * @param tool Herramienta asociada
     * @param type Tipo de kardex
     * @param worker Usuario trabajador que realiza la accion
     */
    private void addKardexEntry(int quantity, ToolEntity tool, KardexTypeEntity type, UserEntity worker) {
        KardexEntity kardexEntry = new KardexEntity();
        kardexEntry.setDateTime(LocalDateTime.now());
        kardexEntry.setQuantity(quantity);
        kardexEntry.setTool(tool);
        kardexEntry.setKardexType(type);
        kardexEntry.setWorkerUser(worker);
        kardexRepository.save(kardexEntry);
    }

    /**
     * Verifica si una herramienta puede ser desactivada
     *
     * @param tool Herramienta a verificar
     * @throws BusinessException si la herramienta no puede ser desactivada
     */
    private void isToolAvailableForDeactivation(ToolEntity tool) {
        String status = tool.getToolStatus().getName();
        if (!status.equals(STATUS_TOOL_AVAILABLE) && !status.equals(STATUS_TOOL_IN_REPAIR)) {
            throw new BusinessException("Tool must be 'Disponible' or 'En Reparacion' to be deactivated.");
        }
    }


    /* Metodos Mapper */

    /**
     * Mapea una entidad ToolEntity a un DTO ToolDTO
     *
     * @param entity Entidad ToolEntity
     * @return DTO ToolDTO
     */
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

    /**
     * Mapea una proyeccion MostUsedToolProjection a un DTO MostUsedToolDTO
     *
     * @param projection Proyeccion MostUsedToolProjection
     * @return DTO MostUsedToolDTO
     */
    private MostUsedToolDTO toMostUsedToolDTO(MostUsedToolProjection projection) {
        Objects.requireNonNull(projection, "MostUsedToolProjection cannot be null");
        MostUsedToolDTO dto = new MostUsedToolDTO();
        dto.setId(projection.getToolId());
        dto.setName(projection.getToolName());
        dto.setModel(projection.getToolModel());
        dto.setBrand(projection.getToolBrand());
        dto.setUsageCount(projection.getUsageCount());
        return dto;
    }

    /**
     * Mapea una entidad ToolStatusEntity a un DTO ToolStatusDTO
     *
     * @param entity Entidad ToolStatusEntity
     * @return DTO ToolStatusDTO
     */
    private ToolStatusDTO toToolStatusDTO(ToolStatusEntity entity) {
        Objects.requireNonNull(entity, "ToolStatusEntity cannot be null");
        ToolStatusDTO dto = new ToolStatusDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    private ToolCategoriesDTO toToolCategoryDTO(ToolCategoryEntity entity) {
        Objects.requireNonNull(entity, "ToolCategoryEntity cannot be null");
        ToolCategoriesDTO dto = new ToolCategoriesDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    private ToolModelsDTO toModelDTO(ModelEntity entity) {
        Objects.requireNonNull(entity, "ModelEntity cannot be null");
        ToolModelsDTO dto = new ToolModelsDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
}
