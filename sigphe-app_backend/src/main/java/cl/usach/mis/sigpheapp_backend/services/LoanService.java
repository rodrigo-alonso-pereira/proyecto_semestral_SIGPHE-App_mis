package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.*;
import cl.usach.mis.sigpheapp_backend.entities.*;
import cl.usach.mis.sigpheapp_backend.exceptions.BusinessException;
import cl.usach.mis.sigpheapp_backend.exceptions.ResourceNotFoundException;
import cl.usach.mis.sigpheapp_backend.repositories.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    // Constantes de estados de prestamo, usuario, herramienta y multa
    private static final String STATUS_USER_WITH_DEBT = "Con Deuda";
    private static final String STATUS_USER_ACTIVE = "Activo";
    private static final String STATUS_LOAN_ACTIVE = "Vigente";
    private static final String STATUS_LOAN_OVERDUE = "Atrasada";
    private static final String STATUS_LOAN_FINISHED = "Finalizado";
    private static final String STATUS_PENALTY_PAID = "Pagada";
    private static final String STATUS_PENALTY_ACTIVE = "Activo";
    private static final String STATUS_TOOL_AVAILABLE = "Disponible";
    private static final String STATUS_TOOL_LOANED = "Prestada";
    private static final String STATUS_TOOL_IN_REPAIR = "En Reparacion";
    private static final String STATUS_TOOL_DECOMMISSIONED = "Dada de baja";

    // Constantes de tipos de kardex
    private static final String TYPE_KARDEX_LOAN = "Prestamo";
    private static final String TYPE_KARDEX_RETURN = "Devolucion";
    private static final String TYPE_KARDEX_REPAIR = "Reparacion";
    private static final String TYPE_KARDEX_DECOMMISSION = "Baja";

    // Constantes de tipos de multa
    private static final String TYPE_PENALTY_REPAIR = "Reparacion";
    private static final String TYPE_PENALTY_IRREPARABLE = "Daño irreparable";
    private static final String TYPE_PENALTY_LATE = "Atraso";

    // Reglas de negocio
    @Value("${app.loan.max}")
    private int MAX_VIGENT_LOANS;

    @Autowired private LoanRepository loanRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LoanStatusRepository loanStatusRepository;
    @Autowired private LoanDetailRepository loanDetailRepository;
    @Autowired private ToolRepository toolRepository;
    @Autowired private ToolStatusRepository toolStatusRepository;
    @Autowired private KardexTypeRepository kardexTypeRepository;
    @Autowired private KardexRepository kardexRepository;
    @Autowired private PenaltyRepository penaltyRepository;
    @Autowired private PenaltyTypeRepository penaltyTypeRepository;
    @Autowired private PenaltyStatusRepository penaltyStatusRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private UserService userService;

    /**
     * Obtiene un resumen de todos los préstamos.
     *
     * @return Lista de LoanDTO que representan los préstamos
     */
    public List<LoanDTO> getAllLoansSummary() {
        return loanRepository.findAllWithRelations().stream()
                .map(this::toLoanDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los préstamos que coinciden con los estados proporcionados.
     *
     * @param statuses Lista de nombres de estados de préstamo
     * @return Lista de LoanDTO que representan los préstamos filtrados
     */
    public List<LoanDTO> getAllLoansByStatuses(List<String> statuses) {
        return loanRepository.findByLoanStatusNameInWithRelations(statuses).stream()
                .map(this::toLoanDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los préstamos que coinciden con los estados y rango de fechas proporcionados.
     *
     * @param statuses Lista de nombres de estados de préstamo
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return Lista de LoanDTO que representan los préstamos filtrados
     */
    public List<LoanDTO> getAllLoansByStatusesAndDateRange(@NotEmpty @NotNull List<String> statuses,
                                                           @NotNull LocalDateTime startDate,
                                                           @NotNull LocalDateTime endDate) {
        return loanRepository.findAllByLoanStatusNameInAndStartDateBetweenOrderByStartDateDesc(statuses,
                        startDate, endDate).stream()
                .map(this::toLoanDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los detalles de un préstamo por su ID.
     *
     * @param id ID del préstamo
     * @return LoanDetailDTO que representa los detalles del préstamo
     * @throws ResourceNotFoundException si no se encuentra el préstamo
     */
    public LoanDetailDTO getLoanDetailById(@NotNull Long id) {
        LoanEntity loan = getLoanById(id); // Obtener la entidad Loan
        List<LoanDetailEntity> loanDetails = getLoanDetailsByLoanId(id); // Obtener detalles del préstamo
        UserEntity customer = loan.getCustomerUser(); // Obtener el cliente asociado al préstamo
        List<ToolDTO> tools = new ArrayList<>();
        for (LoanDetailEntity detail : loanDetails) {
            // Aprovechar el fetch eager de LoanDetail -> Tool (@ManyToOne) para evitar consultas adicionales
            tools.add(toToolDTO(detail.getTool())); // Obtiene el DTO de la herramienta asociada al detalle
        }
        LoanDetailDTO loanDetailDTO = toLoanDetailDTO(loan); // Convierte LoanEntity a LoanDetailDTO
        loanDetailDTO.setTools(tools); // Asigna la lista de herramientas al DTO
        loanDetailDTO.setCustomer(toUserLoanDTO(customer)); // Asigna el cliente al DTO
        return loanDetailDTO;
    }

    /**
     * Crea un nuevo préstamo basado en la información proporcionada.
     *
     * @param dto DTO que contiene la información para crear el préstamo
     * @return LoanDTO que representa el préstamo creado
     * @throws BusinessException si alguna validación de negocio falla
     */
    @Transactional
    public LoanDTO createLoan(CreateLoanRequestDTO dto) {
        UserEntity worker = getUserById(dto.getWorkerId()); // Obtener worker
        UserEntity customer = getUserById(dto.getCustomerId()); // Obtener customer

        // Validar que el cliente sea elegible para un nuevo préstamo según reglas de negocio
        validateCustomerEligibilityForNewLoan(customer);

        // Obtener entidades necesarias para el préstamo
        LoanStatusEntity loanStatus = getLoanStatusByName(STATUS_LOAN_ACTIVE);
        ToolStatusEntity toolStatus = getToolStatusByName(STATUS_TOOL_LOANED);
        KardexTypeEntity kardexType = getKardexTypeByName(TYPE_KARDEX_LOAN);

        // Preparacion para calcular el valor total del alquiler
        BigDecimal totalRental = BigDecimal.ZERO;
        long rentalDays = (long) Math.ceil((double) Duration.between(
                LocalDateTime.now(), dto.getDueDate()).toHours() / 24); // Calcular días de alquiler redondeando (up)
        if (rentalDays <= 1) rentalDays = 1; // Asegurar al menos un día de alquiler
        List<Long> toolModelsInLoan = new ArrayList<>(); // Lista auxiliar para validar modelos de herramientas
        for (Long toolId : dto.getToolIds()) {
            ToolEntity tool = getToolById(toolId); // Obtener herramienta

            // Validar que la herramienta esté disponible
            if (!tool.getToolStatus().getName().equals(STATUS_TOOL_AVAILABLE)) {
                throw new BusinessException("Tool with ID " + toolId + " is not available");
            }

            // Validar que no se puede tener más de un mismo modelo en el mismo préstamo
            toolModelsInLoan.add(tool.getModel().getId());
            if (toolModelsInLoan.stream().filter(id -> id.equals(tool.getModel().getId())).count() > 1) {
                throw new BusinessException("Cannot have more than one tool of the same model '" +
                        tool.getModel().getName() + "' in a single loan");
            }

            // Calcular el valor del alquiler de herramienta actual
            BigDecimal rentalValue = tool.getRentalValue(); // Obtener valor de alquiler
            totalRental = totalRental.add(rentalValue.multiply(BigDecimal.valueOf(rentalDays)
                    .setScale(2, RoundingMode.CEILING))); // Sumar al total
        }

        // Crear la entidad Loan -> asignar valores iniciales -> guardar
        LoanEntity newLoan = new LoanEntity();
        newLoan.setCustomerUser(customer);
        newLoan.setStartDate(LocalDateTime.now()); // Fecha y hora actual
        newLoan.setDueDate(dto.getDueDate());
        newLoan.setLoanStatus(loanStatus);
        newLoan.setTotalRental(totalRental);
        LoanEntity savedLoan = loanRepository.save(newLoan); // Guardar el préstamo para generar ID

        // Procesar cada herramienta y crear detalles
        for (Long idTool : dto.getToolIds()) {
            // Obtener la herramienta
            ToolEntity tool = getToolById(idTool);

            // Cambiar el estado de la herramienta
            tool.setToolStatus(toolStatus); // toolStatus -> "Prestada"
            toolRepository.save(tool);

            // Registrar movimiento en Kardex
            addKardexEntry(-1, tool, kardexType, worker); // -1 -> prestamo

            // Registrar el detalle del prestamo (Loan se asigna x addLoanDetail)
            LoanDetailEntity loanDetail = new LoanDetailEntity(); // Crear el detalle del préstamo
            loanDetail.setTool(tool); // Asociar la herramienta al detalle
            loanDetail.setLoan(savedLoan); // Asociar el préstamo al detalle
            loanDetail.setRentalValueAtTime(tool.getRentalValue()); // Valor de alquiler al momento del préstamo
            //tool.addLoanDetail(loanDetail); // Asociar el detalle a la herramienta con el helper

            savedLoan.addLoanDetail(loanDetail); // Asociar el detalle al préstamo con el helper
        }
        LoanEntity finalLoan = loanRepository.save(savedLoan); // Guardar el préstamo junto con sus detalles
        return toLoanDTO(finalLoan); // Convertir y retornar el DTO del préstamo creado
    }

    /**
     * Procesa la devolución de un préstamo.
     *
     * @param id ID del préstamo a devolver
     * @param dto DTO que contiene la información para procesar la devolución
     * @return LoanDTO que representa el préstamo actualizado
     * @throws BusinessException si alguna validación de negocio falla
     */
    @Transactional
    public LoanDTO processReturnLoan(@NotNull Long id, ReturnLoanRequestDTO dto) {
        UserEntity worker = getUserById(dto.getWorkerId());
        UserEntity customer = getUserById(dto.getCustomerId());
        LoanEntity loan = getLoanById(id);

        // Validaciones de negocio - lanzan excepciones si no cumplen
        validateLoanBelongsToCustomer(loan, customer);
        validateLoanIsReturnable(loan);

        // Hacer el proceso de devolución de cada herramienta perteneciente al préstamo y calcular multas si aplica
        dto.getToolConditions().forEach((toolId, condition) -> {
            ToolEntity tool = getToolById(toolId);
            KardexEntity kardexEntry; // Inicializa un registro en kardex
            PenaltyEntity penalty = new PenaltyEntity(); // Inicializa una multa si aplica

            // Validar que la herramienta pertenezca al préstamo
            validateToolBelongsToLoan(loan, toolId);

            // Actualizar el estado de la herramienta según la condición reportada
            switch (condition.toLowerCase()) {
                case "ok": // Herramienta en buen estado -> Cambiar a "Disponible" y registrar en kardex
                    tool.setToolStatus(getToolStatusByName(STATUS_TOOL_AVAILABLE));
                    addKardexEntry(1, tool, getKardexTypeByName(TYPE_KARDEX_RETURN), worker); // +1 -> devolucion
                    break;
                case "dañada": // Herramienta dañada -> Cambiar a "En Reparacion", calcular multa y registrar en kardex
                    tool.setToolStatus(getToolStatusByName(STATUS_TOOL_IN_REPAIR));
                    penalty = createAndCalculatePenalty(TYPE_PENALTY_REPAIR, tool.getReplacementValue());
                    penalty.setLoan(loan); // Asociar la multa al préstamo
                    penaltyRepository.save(penalty); // Guardar la multa
                    addKardexEntry(-1, tool, getKardexTypeByName(TYPE_KARDEX_REPAIR), worker); // -1 -> reparacion
                    break;
                case "perdida": // Herramienta perdida -> Cambiar a "Dada de baja", calcular multa y registrar en kardex
                    tool.setToolStatus(getToolStatusByName(STATUS_TOOL_DECOMMISSIONED));
                    penalty = createAndCalculatePenalty(TYPE_PENALTY_IRREPARABLE, tool.getReplacementValue());
                    penalty.setLoan(loan); // Asociar la multa al préstamo
                    penaltyRepository.save(penalty); // Guardar la multa
                    addKardexEntry(-1, tool, getKardexTypeByName(TYPE_KARDEX_DECOMMISSION), worker); // -1 -> perdida
                    break;
                default:
                    throw new BusinessException("Invalid tool condition: " + condition);
            };
            loan.setTotalPenalties(loan.getTotalPenalties().add(penalty.getPenaltyAmount()
                    .setScale(2, RoundingMode.CEILING))); // Actualizar total de multas del préstamo
            toolRepository.save(tool); // Guardar el cambio de estado
        });

        // Cálculo de multas por retraso en la devolución si aplica
        if (loan.getDueDate().isBefore(LocalDateTime.now())) { // Si la fecha actual es posterior a la fecha de vencimiento
            BigDecimal dailyRentalValue;

            // Evalúa que dias de alquiler sean mayor a 0
            if (Duration.between(loan.getStartDate(), loan.getDueDate()).toHours() <= 0) {
                dailyRentalValue = loan.getTotalRental(); // Evitar division por cero
            } else {
                // rentaDia = totalRenta / diasAlquiler
                dailyRentalValue = loan.getTotalRental().divide(
                        BigDecimal.valueOf((long) Math.ceil((double)
                                Duration.between(loan.getStartDate(), loan.getDueDate()).toHours() / 24)),
                        2, RoundingMode.CEILING);
            }

            // diasAtraso = ceil((fechaActual - fechaVencimiento) / 24 horas)
            long daysLate = (long) Math.ceil((double)
                    Duration.between(loan.getDueDate(), LocalDateTime.now()).toHours() / 24);
            if (daysLate <= 1) daysLate = 1; // Asegurar al menos un día de retraso

            // Crear y calcular la multa por atraso
            PenaltyEntity latePenalty = createAndCalculatePenalty(TYPE_PENALTY_LATE,
                    (dailyRentalValue.multiply(BigDecimal.valueOf(daysLate)
                            .setScale(2, RoundingMode.CEILING)))); // Valor base = valorRentaDia * diasAtraso

            latePenalty.setLoan(loan); // Asociar la multa al préstamo
            penaltyRepository.save(latePenalty); // Guardar la multa

            // TotalPenalties = Penalidades anteriores (si aplica) + multaAtraso
            loan.setTotalPenalties(loan.getTotalPenalties().add(latePenalty.getPenaltyAmount()
                    .setScale(2, RoundingMode.CEILING)));
        }

        loan.setReturnDate(LocalDateTime.now()); // Establecer la fecha de devolución
        loan.setLoanStatus(getLoanStatusByName(STATUS_LOAN_OVERDUE)); // Cambiar estado del préstamo
        loanRepository.save(loan); // Guardar el préstamo actualizado
        customer.setUserStatus(getUserStatusByName(STATUS_USER_WITH_DEBT));// Cliente -> "Con Deuda"
        userRepository.save(customer); // Guardar el cliente actualizado
        return toLoanDTO(loan); // Guardar y retornar el DTO del préstamo actualizado
    }

    /**
     * Procesa el pago de un préstamo.
     *
     * @param id ID del préstamo a pagar
     * @param dto DTO que contiene la información para procesar el pago
     * @return LoanDTO que representa el préstamo actualizado
     * @throws BusinessException si alguna validación de negocio falla
     */
    @Transactional
    public LoanDTO processPayment(@NotNull Long id, PaymentLoanRequestDTO dto) {
        UserEntity customer = getUserById(dto.getCustomerId());
        LoanEntity loan = getLoanById(id);

        // Validaciones de negocio - lanzan excepciones si no cumplen
        validateLoanBelongsToCustomer(loan, customer);
        validateLoanIsPayable(loan);
        validatePaymentAmount(loan, dto.getPaymentAmount());

        // Procesar el pago del préstamo y actualizar estados
        for (PenaltyEntity penalty : loan.getPenalties()) { // Por cada penalty asociada al préstamo
            penalty.setPenaltyStatus(getPenaltyStatusByName(STATUS_PENALTY_PAID));
            penaltyRepository.save(penalty); // Guardar el cambio de estado
        }
        loan.setPaymentDate(LocalDateTime.now()); // Establecer la fecha de pago
        loan.setLoanStatus(getLoanStatusByName(STATUS_LOAN_FINISHED)); // Cambiar estado del préstamo
        loanRepository.save(loan); // Guardar el préstamo actualizado

        // Actualizar el estado del cliente si ya no tiene préstamos atrasados
        if (!validateIfCustomerHasOverdueLoans(customer)) {
            customer.setUserStatus(getUserStatusByName(STATUS_USER_ACTIVE));// Cliente -> "Activo"
            userRepository.save(customer); // Guardar el cliente actualizado
        }
        return toLoanDTO(loan); // Guardar y retornar el DTO del préstamo actualizado
    }

    // TODO: Hacer metodo para cambiar un prestamo por otro nuevo por error del operario

    // TODO: Hacer metodo para buscar prestamos que esten atrasadas y cambiar el estado automaticamente

    // TODO: Evaluar implementar estado Entregado para Loan cuando cliente devuelve
    //  herramientas y Atrasado cuando se pasa la fecha de devolucion sin pagar

    /* Metodos auxiliares */

    /**
     * Válida si un cliente tiene préstamos atrasados.
     *
     * @param customer El cliente a validar
     * @return true si el cliente tiene préstamos atrasados, false en caso contrario
     */
    private boolean validateIfCustomerHasOverdueLoans(UserEntity customer) {
        List<LoanEntity> loans = loanRepository.findAllByCustomerUserIdEquals(customer.getId());
        for (LoanEntity loan : loans) {
            // Verificar si el préstamo está atrasado y en estado "Vigente" o "Atrasada"
            if (loan.getDueDate().isBefore(LocalDateTime.now()) &&
                    (loan.getLoanStatus().getName().equals(STATUS_LOAN_ACTIVE) ||
                     loan.getLoanStatus().getName().equals(STATUS_LOAN_OVERDUE)))
                return true; // El cliente tiene al menos un préstamo atrasado
        }
        return false; // El cliente no tiene préstamos atrasados
    }

    /**
     * Obtiene una entidad LoanEntity por su ID.
     *
     * @param loanId ID del préstamo
     * @return LoanEntity correspondiente
     * @throws ResourceNotFoundException si no se encuentra el préstamo
     */
    private LoanEntity getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));
    }

    /**
     * Obtiene una entidad UserEntity por su ID.
     *
     * @param userId ID del usuario
     * @return UserEntity correspondiente
     * @throws ResourceNotFoundException si no se encuentra el usuario
     */
    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /**
     * Obtiene una entidad ToolEntity por su ID.
     *
     * @param toolId ID de la herramienta
     * @return ToolEntity correspondiente
     * @throws ResourceNotFoundException si no se encuentra la herramienta
     */
    private ToolEntity getToolById(Long toolId) {
        return toolRepository.findById(toolId)
                .orElseThrow(() -> new ResourceNotFoundException("Tool", "id", toolId));
    }

    private List<LoanDetailEntity> getLoanDetailsByLoanId(Long loanId) {
        return loanDetailRepository.findAllByLoanIdEquals(loanId);
    }

    /**
     * Obtiene una entidad ToolStatusEntity por su nombre.
     *
     * @param name Nombre del estado de herramienta
     * @return ToolStatusEntity correspondiente
     * @throws ResourceNotFoundException si no se encuentra el estado
     */
    private ToolStatusEntity getToolStatusByName(String name) {
        return toolStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tool Status", "name", name));
    }

    /**
     * Obtiene una entidad LoanStatusEntity por su nombre.
     *
     * @param name Nombre del estado de préstamo
     * @return LoanStatusEntity correspondiente
     * @throws ResourceNotFoundException si no se encuentra el estado
     */
    private LoanStatusEntity getLoanStatusByName(String name) {
        return loanStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Loan Status", "name", name));
    }

    /**
     * Obtiene una entidad KardexTypeEntity por su nombre.
     *
     * @param name Nombre del tipo de kardex
     * @return KardexTypeEntity correspondiente
     * @throws ResourceNotFoundException si no se encuentra el tipo
     */
    private KardexTypeEntity getKardexTypeByName(String name) {
        return kardexTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Kardex Type", "name", name));
    }

    /**
     * Obtiene una entidad PenaltyStatusEntity por su nombre.
     *
     * @param name Nombre del estado de multa
     * @return PenaltyStatusEntity correspondiente
     * @throws ResourceNotFoundException si no se encuentra el estado
     */
    private PenaltyStatusEntity getPenaltyStatusByName(String name) {
        return penaltyStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Penalty Status", "name", name));
    }

    /**
     * Obtiene una entidad PenaltyTypeEntity por su nombre.
     *
     * @param name Nombre del tipo de multa
     * @return PenaltyTypeEntity correspondiente
     * @throws ResourceNotFoundException si no se encuentra el tipo
     */
    private PenaltyTypeEntity getPenaltyTypeByName(String name) {
        return penaltyTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Penalty Type", "name", name));
    }

    /**
     * Obtiene una entidad UserStatusEntity por su nombre.
     *
     * @param name Nombre del estado de usuario
     * @return UserStatusEntity correspondiente
     * @throws ResourceNotFoundException si no se encuentra el estado
     */
    private UserStatusEntity getUserStatusByName(String name) {
        return userStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("User Status", "name", name));
    }

    /**
     * Agrega una entrada al kardex.
     *
     * @param quantity Cantidad de herramientas (positiva o negativa)
     * @param tool Herramienta asociada
     * @param type Tipo de movimiento en kardex
     * @param worker Usuario que realiza el movimiento
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
     * Crea y calcula una multa basada en el tipo y valor proporcionados.
     *
     * @param penaltyTypeName Nombre del tipo de multa
     * @param value Valor base para calcular la multa
     * @return PenaltyEntity creada y calculada
     */
    private PenaltyEntity createAndCalculatePenalty(String penaltyTypeName, BigDecimal value) {
        PenaltyEntity penalty = new PenaltyEntity();
        PenaltyTypeEntity penaltyType = getPenaltyTypeByName(penaltyTypeName);
        penalty.setPenaltyAmount(value.multiply(penaltyType.getPenaltyFactor()
                .setScale(2, RoundingMode.CEILING))); // Calcular el monto de la multa
        penalty.setPenaltyDate(LocalDateTime.now());
        penalty.setDescription("Multa por herramienta en estado: '" + penaltyTypeName.toLowerCase()
                + "'. Por valor de: $" + penalty.getPenaltyAmount().setScale(2, RoundingMode.CEILING));
        penalty.setPenaltyType(penaltyType);
        penalty.setPenaltyStatus(getPenaltyStatusByName(STATUS_PENALTY_ACTIVE));
        return penalty;
    };

    /**
     * Valida si un cliente es elegible para un nuevo préstamo.
     *
     * @param customer El cliente a validar
     * @throws BusinessException si el cliente no es elegible (con razón específica)
     */
    private void validateCustomerEligibilityForNewLoan(UserEntity customer) {
        // Validar que el cliente esté activo y no tenga deudas pendientes
        if (customer.getUserStatus().getName().equals(STATUS_USER_WITH_DEBT)) {
            throw new BusinessException("Customer " + customer.getName() + " has outstanding debts");
        }

        List<LoanEntity> loans = loanRepository.findAllByCustomerUserIdEquals(customer.getId());
        List<Long> toolModelsInLoan = new ArrayList<>();

        int vigentLoans = 0;
        for (LoanEntity loan : loans) {
            validateOverdueLoans(loan, customer); // Validar que no tenga préstamos atrasados

            // Contar préstamos vigentes y registrar modelos de herramientas en préstamos vigentes
            if (loan.getLoanStatus().getName().equals(STATUS_LOAN_ACTIVE)) {
                vigentLoans++;
                toolModelsInLoan.add(loan.getLoanDetails().stream()
                        .map(detail -> detail.getTool().getModel().getId())
                        .findFirst()
                        .orElse(-1L));
            }
        }

        // Validar que no se puede tener más de un mismo modelo en préstamos vigentes
        for (Long modelId : toolModelsInLoan) {
            if (toolModelsInLoan.stream().filter(id -> id.equals(modelId)).count() > 1) {
                throw new BusinessException("Customer " + customer.getName() +
                        " can't have more than one tool of the same model ID: " + modelId +
                        " in different loans");
            }
        }

        // Validar cantidad máxima de préstamos vigentes no exceda el límite
        if (vigentLoans >= MAX_VIGENT_LOANS) {
            throw new BusinessException("Customer " + customer.getName() +
                    " can't have more than " + MAX_VIGENT_LOANS + " active loans");
        }
    }

    /**
     * Valida que un préstamo pertenezca al cliente indicado.
     *
     * @param loan El préstamo a validar
     * @param customer El cliente a validar
     * @throws BusinessException si el préstamo no pertenece al cliente
     */
    private void validateLoanBelongsToCustomer(LoanEntity loan, UserEntity customer) {
        if (!loan.getCustomerUser().getId().equals(customer.getId())) {
            throw new BusinessException("Loan ID " + loan.getId() + " does not belong to customer ID "
                    + customer.getId() + ": " + customer.getName() );
        }
    }

    /**
     * Valida que un préstamo esté en estado retornable (Vigente).
     *
     * @param loan El préstamo a validar
     * @throws BusinessException si el préstamo no está en estado retornable
     */
    private void validateLoanIsReturnable(LoanEntity loan) {
        if (!loan.getLoanStatus().getName().equals(STATUS_LOAN_ACTIVE)) {
            throw new BusinessException("Loan ID " + loan.getId() + " is not in a returnable status. " +
                    "Current status: " + loan.getLoanStatus().getName());
        }
    }

    /**
     * Valida que un préstamo esté en estado pagable (Atrasada).
     *
     * @param loan El préstamo a validar
     * @throws BusinessException si el préstamo no está en estado pagable
     */
    private void validateLoanIsPayable(LoanEntity loan) {
        if (!loan.getLoanStatus().getName().equals(STATUS_LOAN_OVERDUE)) {
            throw new BusinessException("Loan ID " + loan.getId() + " is not in a payable status. " +
                    "Current status: " + loan.getLoanStatus().getName());
        }
    }

    /**
     * Valida que una herramienta pertenezca al préstamo.
     *
     * @param loan El préstamo
     * @param toolId El ID de la herramienta
     * @throws BusinessException si la herramienta no pertenece al préstamo
     */
    private void validateToolBelongsToLoan(LoanEntity loan, Long toolId) {
        boolean toolBelongsToLoan = loan.getLoanDetails().stream()
                .anyMatch(detail -> detail.getTool().getId().equals(toolId));

        if (!toolBelongsToLoan) {
            throw new BusinessException("Tool ID " + toolId + " is not part of loan ID " + loan.getId());
        }
    }

    /**
     * Valida que el monto del pago coincida con el total adeudado.
     *
     * @param loan El préstamo
     * @param paymentAmount El monto del pago
     * @throws BusinessException si el monto no coincide
     */
    private void validatePaymentAmount(LoanEntity loan, double paymentAmount) {
        BigDecimal totalDue = loan.getTotalRental()
                .add(loan.getTotalPenalties())
                .setScale(2, RoundingMode.CEILING);
        BigDecimal payment = BigDecimal.valueOf(paymentAmount).setScale(2, RoundingMode.CEILING);

        if (!totalDue.equals(payment)) {
            throw new BusinessException("Payment amount $" + payment + " does not match total due $"
                    + totalDue);
        }
    }

    /**
     * Válida si un préstamo está atrasado y actualiza el estado del cliente si aplica.
     *
     * @param loan El préstamo a validar
     * @param customer El cliente asociado al préstamo
     * @throws BusinessException si el préstamo está atrasado
     */
    private void validateOverdueLoans(LoanEntity loan, UserEntity customer) {
        if (loan.getDueDate().isBefore(LocalDateTime.now()) &&
                !(loan.getLoanStatus().getName().equals(STATUS_LOAN_FINISHED))) {
            userService.updateCostumerStatus(customer);
            throw new BusinessException("Customer " + customer.getName() + " has overdue loans");
        }
    }

    /* Metodos Mapper */

    /**
     * Convierte una entidad LoanEntity a su correspondiente DTO LoanDTO.
     *
     * @param loan La entidad LoanEntity a convertir
     * @return El DTO LoanDTO resultante
     */
    private LoanDTO toLoanDTO(LoanEntity loan) {
        Objects.requireNonNull(loan, "LoanEntity cannot be null");
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setStartDate(loan.getStartDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setDueDate(loan.getDueDate());
        dto.setPaymentDate(loan.getPaymentDate());
        dto.setTotalAmount(loan.getTotalRental());
        dto.setTotalPenalties(loan.getTotalPenalties());
        // loanStatus y customerName pueden ser null, por eso se usa Optional
        dto.setLoanStatus(Optional.ofNullable(loan.getLoanStatus())
                .map(LoanStatusEntity::getName)
                .orElse("Unknown"));
        dto.setCustomerName(Optional.ofNullable(loan.getCustomerUser())
                .map(UserEntity::getName)
                .orElse("Unknown"));
        return dto;
    }

    /**
     * Convierte una entidad ToolEntity a su correspondiente DTO ToolDTO.
     *
     * @param tool La entidad ToolEntity a convertir
     * @return El DTO ToolDTO resultante
     */
    private ToolDTO toToolDTO(ToolEntity tool) {
        Objects.requireNonNull(tool, "ToolEntity cannot be null");
        ToolDTO dto = new ToolDTO();
        dto.setId(tool.getId());
        dto.setName(tool.getName());
        dto.setRentalValue(tool.getRentalValue());
        dto.setReplacementValue(tool.getReplacementValue());
        // category, model y status pueden ser null, por eso se usa Optional
        dto.setCategory(Optional.ofNullable(tool.getToolCategory())
                .map(ToolCategoryEntity::getName)
                .orElse("Unknown"));
        dto.setModel(Optional.ofNullable(tool.getModel())
                .map(ModelEntity::getName)
                .orElse("Unknown"));
        dto.setStatus(Optional.ofNullable(tool.getToolStatus())
                .map(ToolStatusEntity::getName)
                .orElse("Unknown"));
        return dto;
    }

    /**
     * Convierte una entidad LoanEntity a su correspondiente DTO LoanDetailDTO.
     *
     * @param loan La entidad LoanEntity a convertir
     * @return El DTO LoanDetailDTO resultante
     */
    private LoanDetailDTO toLoanDetailDTO(LoanEntity loan) {
        Objects.requireNonNull(loan, "LoanEntity cannot be null");
        LoanDetailDTO dto = new LoanDetailDTO();
        dto.setId(loan.getId());
        dto.setStartDate(loan.getStartDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setDueDate(loan.getDueDate());
        dto.setPaymentDate(loan.getPaymentDate());
        dto.setTotalAmount(loan.getTotalRental());
        dto.setTotalPenalties(loan.getTotalPenalties());
        // loanStatus y customerName pueden ser null, por eso se usa Optional
        dto.setLoanStatus(Optional.ofNullable(loan.getLoanStatus())
                .map(LoanStatusEntity::getName)
                .orElse("Unknown"));
        return dto;
    }

    /**
     * Convierte una entidad UserEntity a su correspondiente DTO UserLoanDTO.
     *
     * @param customer La entidad UserEntity a convertir
     * @return El DTO UserLoanDTO resultante
     */
    private UserLoanDTO toUserLoanDTO(UserEntity customer) {
        Objects.requireNonNull(customer, "UserEntity cannot be null");
        UserLoanDTO dto = new UserLoanDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        return dto;
    }
}
