package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.CreateLoanRequestDTO;
import cl.usach.mis.sigpheapp_backend.dtos.LoanDTO;
import cl.usach.mis.sigpheapp_backend.dtos.PaymentLoanRequestDTO;
import cl.usach.mis.sigpheapp_backend.dtos.ReturnLoanRequestDTO;
import cl.usach.mis.sigpheapp_backend.entities.*;
import cl.usach.mis.sigpheapp_backend.repositories.*;
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

    // Status constants
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

    // Kardex type constants
    private static final String TYPE_KARDEX_LOAN = "Prestamo";
    private static final String TYPE_KARDEX_RETURN = "Devolucion";
    private static final String TYPE_KARDEX_REPAIR = "Reparacion";
    private static final String TYPE_KARDEX_DECOMMISSION = "Baja";

    // Penalty type constants
    private static final String TYPE_PENALTY_REPAIR = "Reparacion";
    private static final String TYPE_PENALTY_IRREPARABLE = "Daño irreparable";
    private static final String TYPE_PENALTY_LATE = "Atraso";

    // Business rules constants
    @Value("${app.loan.max}")
    private int MAX_VIGENT_LOANS;

    @Autowired private LoanRepository loanRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LoanStatusRepository loanStatusRepository;
    @Autowired private ToolRepository toolRepository;
    @Autowired private ToolStatusRepository toolStatusRepository;
    @Autowired private KardexTypeRepository kardexTypeRepository;
    @Autowired private KardexRepository kardexRepository;
    @Autowired private PenaltyRepository penaltyRepository;
    @Autowired private PenaltyTypeRepository penaltyTypeRepository;
    @Autowired private PenaltyStatusRepository penaltyStatusRepository;
    @Autowired private UserStatusRepository userStatusRepository;

    public List<LoanDTO> getAllLoansSummary() {
        return loanRepository.findAll().stream()
                .map(this::toLoanDTO)
                .collect(Collectors.toList());
    }

    public List<LoanDTO> getAllLoansByStatuses(List<String> statuses) {
        return loanRepository.findByLoanStatusNameIn(statuses).stream()
                .map(this::toLoanDTO)
                .collect(Collectors.toList());
    }

    public List<LoanDTO> getAllLoansByCustomerId(Long customerId) {
        return loanRepository.findAllById(customerId).stream()
                .map(this::toLoanDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoanDTO createLoan(CreateLoanRequestDTO dto) {
        UserEntity worker = getUserById(dto.getWorkerId()); // Obtener worker
        UserEntity customer = getUserById(dto.getCustomerId()); // Obtener customer

        // Validar que el cliente sea elegible para un nuevo préstamo según reglas de negocio
        if (!isCustomerEligibleForNewLoan(customer)) {
            throw new IllegalStateException("Customer " + customer.getName() + " is not eligible for a new loan");
        }

        // Obtener entidades necesarias para el préstamo
        LoanStatusEntity loanStatus = getLoanStatusByName(STATUS_LOAN_ACTIVE);
        ToolStatusEntity toolStatus = getToolStatusByName(STATUS_TOOL_LOANED);
        KardexTypeEntity kardexType = getKardexTypeByName(TYPE_KARDEX_LOAN);

        // Preparacion para calcular el valor total del alquiler
        BigDecimal totalRental = BigDecimal.ZERO;
        long rentalDays = (long) Math.ceil((double) Duration.between(
                LocalDateTime.now(), dto.getDueDate()).toHours() / 24); // Calcular días de alquiler redondeando hacia arriba
        if (rentalDays <= 1) rentalDays = 1; // Asegurar al menos un día de alquiler
        List<Long> toolModelsInLoan = new ArrayList<>(); // Lista auxiliar para validar modelos de herramientas
        for (Long toolId : dto.getToolIds()) {
            ToolEntity tool = getToolById(toolId); // Obtener herramienta

            // Validar que la herramienta esté disponible
            if (!tool.getToolStatus().getName().equals(STATUS_TOOL_AVAILABLE)) {
                throw new IllegalStateException("Tool with ID " + toolId + " is not available");
            }

            // Validar que no se puede tener más de un mismo modelo en el mismo préstamo
            toolModelsInLoan.add(tool.getModel().getId());
            if (toolModelsInLoan.stream().filter(id -> id.equals(tool.getModel().getId())).count() > 1) {
                throw new IllegalStateException("Cannot have more than one tool of the same model '" +
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
            ToolEntity tool = toolRepository.findById(idTool)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid tool ID: " + idTool));

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

    // TODO: Evaluar cambios con metodos helper

    @Transactional
    public LoanDTO processReturnLoan(@NotNull Long id, ReturnLoanRequestDTO dto) {
        UserEntity worker = getUserById(dto.getWorkerId()); // Obtiene worker
        UserEntity customer = getUserById(dto.getCustomerId()); // Obtiene customer
        LoanEntity loan = getLoanById(id); // Obtiene préstamo

        // Validar que el préstamo pertenezca al cliente indicado
        if (doesLoanBelongToCustomer(loan, customer.getId())) {
            throw new IllegalStateException("Loan ID " + id + " does not belong to customer ID " + customer.getId());
        }

        // Validar que el estado del préstamo permita realizar la devolución
        if (isLoanStatusIn(loan, STATUS_LOAN_ACTIVE)) {
            throw new IllegalStateException("Loan ID " + id + " is not in a returnable status");
        }


        // Hacer el proceso de devolución de cada herramienta perteneciente al préstamo y calcular multas si aplica
        dto.getToolConditions().forEach((toolId, condition) -> {
            ToolEntity tool = getToolById(toolId); // Obtener herramienta
            KardexEntity kardexEntry; // Inicializa un registro en kardex
            PenaltyEntity penalty = new PenaltyEntity(); // Inicializa una multa si aplica

            // Validar que la herramienta pertenezca al préstamo
            if (!loan.getLoanDetails().stream()
                    .anyMatch(detail -> detail.getTool().getId().equals(toolId))) {
                throw new IllegalStateException("Tool ID " + toolId + " is not part of loan ID " + id);
            }

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
                    throw new IllegalArgumentException("Invalid tool condition: " + condition);
            };
            loan.setTotalPenalties(loan.getTotalPenalties().add(penalty.getPenaltyAmount()
                    .setScale(2, RoundingMode.CEILING))); // Actualizar total de multas del préstamo
            toolRepository.save(tool); // Guardar el cambio de estado
            //kardexRepository.save(kardexEntry); // Guardar el registro en kardex
        });

        // Cálculo de multas por retraso en la devolución si aplica
        if (loan.getDueDate().isBefore(LocalDateTime.now())) { // Si la fecha actual es posterior a la fecha de vencimiento
            long daysLate = (long) Math.ceil((double)
                    Duration.between(loan.getDueDate(), LocalDateTime.now()).toHours() / 24); // Calcular días de retraso
            if (daysLate <= 1) daysLate = 1; // Asegurar al menos un día de retraso
            PenaltyEntity latePenalty = createAndCalculatePenalty(TYPE_PENALTY_LATE,
                    (loan.getTotalRental().multiply(BigDecimal.valueOf(daysLate)
                            .setScale(2, RoundingMode.CEILING)))); // Crear multa por atraso
            latePenalty.setLoan(loan); // Asociar la multa al préstamo
            penaltyRepository.save(latePenalty); // Guardar la multa
            loan.setTotalPenalties(loan.getTotalPenalties().add(latePenalty.getPenaltyAmount()
                    .setScale(2, RoundingMode.CEILING))); // Actualizar total de multas del préstamo
        }

        loan.setReturnDate(LocalDateTime.now()); // Establecer la fecha de devolución
        loan.setLoanStatus(getLoanStatusByName(STATUS_LOAN_OVERDUE)); // Cambiar estado del préstamo
        loanRepository.save(loan); // Guardar el préstamo actualizado
        customer.setUserStatus(getUserStatusByName(STATUS_USER_WITH_DEBT));// Cliente -> "Con Deuda"
        userRepository.save(customer); // Guardar el cliente actualizado
        return toLoanDTO(loan); // Guardar y retornar el DTO del préstamo actualizado
    }

    @Transactional
    public LoanDTO processPayment(@NotNull Long id, PaymentLoanRequestDTO dto) {
        UserEntity customer = getUserById(dto.getCustomerId()); // Obtiene customer
        LoanEntity loan = getLoanById(id); // Obtiene préstamo

        // Validar que el préstamo pertenezca al cliente indicado
        if (doesLoanBelongToCustomer(loan, customer.getId())) {
            throw new IllegalStateException("Loan ID " + id + " does not belong to customer ID " + customer.getId());
        }

        // Validar que el estado del préstamo permita realizar el pago
        if (isLoanStatusIn(loan, STATUS_LOAN_OVERDUE)) {
            throw new IllegalStateException("Loan ID " + id + " is not in a payable status");
        }

        // Validar que el monto del pago coincida con el total adeudado (alquiler + multas)
        if (!loan.getTotalRental().add(loan.getTotalPenalties()).setScale(2, RoundingMode.CEILING)
                .equals(BigDecimal.valueOf(dto.getPaymentAmount()).setScale(2, RoundingMode.CEILING))) {
            throw new IllegalArgumentException("Payment amount does not match total due $" +
                    loan.getTotalRental().add(loan.getTotalPenalties()).setScale(2, RoundingMode.CEILING));
        }

        // Procesar el pago del préstamo y actualizar estados
        for (PenaltyEntity penalty : loan.getPenalties()) { // Por cada penalty asociada al préstamo
            penalty.setPenaltyStatus(getPenaltyStatusByName(STATUS_PENALTY_PAID));
            penaltyRepository.save(penalty); // Guardar el cambio de estado
        }
        loan.setPaymentDate(LocalDateTime.now()); // Establecer la fecha de pago
        loan.setLoanStatus(getLoanStatusByName(STATUS_LOAN_FINISHED)); // Cambiar estado del préstamo
        loanRepository.save(loan); // Guardar el préstamo actualizado
        customer.setUserStatus(getUserStatusByName(STATUS_USER_ACTIVE));// Cliente -> "Activo"
        userRepository.save(customer); // Guardar el cliente actualizado
        return toLoanDTO(loan); // Guardar y retornar el DTO del préstamo actualizado
    }

    // TODO: Hacer metodo para cambiar un prestamo por otro nuevo por error del operario

    /* Metodos auxiliares */

    private LoanEntity getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan ID: " + loanId));
    }

    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
    }

    private ToolEntity getToolById(Long toolId) {
        return toolRepository.findById(toolId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tool ID: " + toolId));
    }

    private ToolStatusEntity getToolStatusByName(String name) {
        return toolStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tool status name: " + name));
    }

    private LoanStatusEntity getLoanStatusByName(String name) {
        return loanStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan status name: " + name));
    }

    private KardexTypeEntity getKardexTypeByName(String name) {
        return kardexTypeRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid kardex type name: " + name));
    }

    private PenaltyStatusEntity getPenaltyStatusByName(String name) {
        return penaltyStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid penalty status name: " + name));
    }

    private PenaltyTypeEntity getPenaltyTypeByName(String name) {
        return penaltyTypeRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid penalty type name: " + name));
    }

    private UserStatusEntity getUserStatusByName(String name) {
        return userStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user status name: " + name));
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

    private boolean doesLoanBelongToCustomer(LoanEntity loan, Long customerId) {
        return !loan.getCustomerUser().getId().equals(customerId);
    }

    private boolean isLoanStatusIn(LoanEntity loan, String statuses) {
        return !statuses.contains(loan.getLoanStatus().getName());
    }

    public boolean isCustomerEligibleForNewLoan(UserEntity customer) {
        // Validar que el cliente esté activo y no tenga deudas pendientes
        if (customer.getUserStatus().getName().equals(STATUS_USER_WITH_DEBT)) {
            throw new IllegalStateException("Customer " + customer.getName() + " has outstanding debts");
        }
        List<LoanEntity> loans = loanRepository.findAllByCustomerUserIdEquals(customer.getId()); // Lista de prestamos del cliente
        List<Long> toolModelsInLoan = new ArrayList<>(); // Lista auxiliar para validar modelos de herramientas

        int vigentLoans = 0;
        for (LoanEntity loan : loans) {
            // Validar que no tenga préstamos atrasados
            if (loan.getDueDate().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("Customer " + customer.getName() + " has overdue loans");
            }
            // Error: No entra a loans con estatus vigenteq
            if (loan.getLoanStatus().getName().equals(STATUS_LOAN_ACTIVE)) {
                vigentLoans++;
                toolModelsInLoan.add(loan.getLoanDetails().stream()
                        .map(detail -> detail.getTool().getModel().getId())
                        .findFirst()
                        .orElse(-1L)); // Agregar modelo de herramienta del préstamo vigente
            }
        }

        // Validar que no se puede tener más de un mismo modelo en préstamos vigentes
        for (Long modelId : toolModelsInLoan) {
            if (toolModelsInLoan.stream().filter(id -> id.equals(modelId)).count() > 1) {
                throw new IllegalStateException("Customer " + customer.getName() +
                        "can't have more than one tool of the same model ID: " + modelId + " in different loans");
            }
        }

        // Validar cantidad máxima de préstamos vigentes no exceda el límite
        if (vigentLoans >= MAX_VIGENT_LOANS) {
            throw new IllegalStateException("Customer " + customer.getName() +
                    " can't have more than " + MAX_VIGENT_LOANS + " active loans");
        }

        return true;
    }

    /* Metodos Mapper */

    // LoanEntity -> LoanDTO
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
}
