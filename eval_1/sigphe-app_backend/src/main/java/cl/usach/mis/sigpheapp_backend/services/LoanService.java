package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.CreateLoanRequestDTO;
import cl.usach.mis.sigpheapp_backend.dtos.LoanSummaryDTO;
import cl.usach.mis.sigpheapp_backend.dtos.ReturnLoanRequestDTO;
import cl.usach.mis.sigpheapp_backend.entities.*;
import cl.usach.mis.sigpheapp_backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired private UserService userService;
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

    public List<LoanSummaryDTO> getAllLoansSummary() {
        return loanRepository.findAll().stream()
                .map(this::toLoanSummaryDTO)
                .collect(Collectors.toList());
    }

    public List<LoanSummaryDTO> getAllLoansByStatuses(List<String> statuses) {
        return loanRepository.findByLoanStatusNameIn(statuses).stream()
                .map(this::toLoanSummaryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoanSummaryDTO createLoan(CreateLoanRequestDTO dto) {
        UserEntity worker = getUserById(dto.getWorkerId()); // Obtener worker
        UserEntity customer = getUserById(dto.getCustomerId()); // Obtener customer

        // Validar que el cliente sea elegible para un nuevo préstamo según reglas de negocio
        if (!userService.isCustomerEligibleForNewLoan(customer.getId())) {
            throw new IllegalStateException("Customer " + customer.getName() + " is not eligible for a new loan");
        }

        // Obtener entidades necesarias para el préstamo
        LoanStatusEntity loanStatus = getLoanStatusByName("Vigente");
        ToolStatusEntity toolStatus = getToolStatusByName("Prestada");
        KardexTypeEntity kardexType = getKardexTypeByName("Prestamo");

        // Preparacion para calcular el valor total del alquiler
        BigDecimal totalRental = BigDecimal.ZERO;
        long rentalDays = (long) Math.ceil((double) Duration.between(
                LocalDateTime.now(), dto.getDueDate()).toHours() / 24); // Calcular días de alquiler redondeando hacia arriba
        if (rentalDays <= 1) rentalDays = 1; // Asegurar al menos un día de alquiler
        for (Long toolId : dto.getToolIds()) {
            ToolEntity tool = getToolById(toolId); // Obtener herramienta

            // Validar que la herramienta esté disponible
            if (!tool.getToolStatus().getName().equals("Disponible")) {
                throw new IllegalStateException("Tool with ID " + toolId + " is not available");
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
            KardexEntity kardexEntry = addKardexEntry(-1, tool, kardexType, worker); // -1 -> prestamo
            kardexRepository.save(kardexEntry);

            // Registrar el detalle del prestamo (Loan se asigna x addLoanDetail)
            LoanDetailEntity loanDetail = new LoanDetailEntity(); // Crear el detalle del préstamo
            loanDetail.setTool(tool); // Asociar la herramienta al detalle
            loanDetail.setLoan(savedLoan); // Asociar el préstamo al detalle
            loanDetail.setRentalValueAtTime(tool.getRentalValue()); // Valor de alquiler al momento del préstamo

            savedLoan.addLoanDetail(loanDetail); // Asociar el detalle al préstamo con el helper
        }
        LoanEntity finalLoan = loanRepository.save(savedLoan); // Guardar el préstamo junto con sus detalles
        return toLoanSummaryDTO(finalLoan); // Convertir y retornar el DTO del préstamo creado
    }

    @Transactional
    public LoanSummaryDTO processReturnLoan(Long id, ReturnLoanRequestDTO dto) {
        UserEntity worker = getUserById(dto.getWorkerId()); // Obtiene worker
        UserEntity customer = getUserById(dto.getCustomerId()); // Obtiene customer
        LoanEntity loan = getLoanById(id); // Obtiene préstamo

        // Validar que el préstamo pertenezca al cliente indicado
        if (!doesLoanBelongToCustomer(loan, customer.getId())) {
            throw new IllegalStateException("Loan ID " + id + " does not belong to customer ID " + customer.getId());
        }

        // Hacer el proceso de devolución de cada herramienta perteneciente al préstamo y calcular multas si aplica
        dto.getToolConditions().forEach((condition, toolId) -> {
            ToolEntity tool = getToolById(toolId); // Obtener herramienta
            KardexEntity kardexEntry = new KardexEntity(); // Inicializa un registro en kardex
            ToolStatusEntity newStatus = new ToolStatusEntity(); // Inicializa un nuevo estado de herramienta
            PenaltyEntity penalty = new PenaltyEntity(); // Inicializa una multa si aplica

            // Validar que la herramienta pertenezca al préstamo
            if (loan.getLoanDetails().stream()
                    .noneMatch(detail -> detail.getTool().getId().equals(tool.getId()))) {
                throw new IllegalStateException("Tool ID " + toolId + " is not part of loan ID " + id);
            }

            // Actualizar el estado de la herramienta según la condición reportada
            switch (condition.toLowerCase()) {
                case "ok": // Herramienta en buen estado -> Cambiar a "Disponible" y registrar en kardex
                    tool.setToolStatus(getToolStatusByName("Disponible"));
                    kardexEntry = addKardexEntry(1, tool, getKardexTypeByName("Devolucion"), worker); // +1 -> devolucion
                    break;
                case "dañada": // Herramienta dañada -> Cambiar a "En Reparacion", calcular multa y registrar en kardex
                    tool.setToolStatus(getToolStatusByName("En Reparacion"));
                    penalty = createAndCalculatePenalty("Reparacion", tool.getReplacementValue());
                    penalty.setLoan(loan); // Asociar la multa al préstamo
                    penaltyRepository.save(penalty); // Guardar la multa
                    kardexEntry = addKardexEntry(-1, tool, getKardexTypeByName("Reparacion"), worker); // -1 -> reparacion
                    break;
                case "perdida": // Herramienta perdida -> Cambiar a "Dada de baja", calcular multa y registrar en kardex
                    tool.setToolStatus(getToolStatusByName("Dada de baja"));
                    penalty = createAndCalculatePenalty("Daño irreparable", tool.getReplacementValue());
                    penalty.setLoan(loan); // Asociar la multa al préstamo
                    penaltyRepository.save(penalty); // Guardar la multa
                    kardexEntry = addKardexEntry(-1, tool, getKardexTypeByName("Baja"), worker); // -1 -> perdida
                    break;
                default:
                    throw new IllegalArgumentException("Invalid tool condition: " + condition);
            };
            loan.setTotalPenalties(loan.getTotalPenalties().add(penalty.getPenaltyAmount()
                    .setScale(2, RoundingMode.CEILING))); // Actualizar total de multas del préstamo
            toolRepository.save(tool); // Guardar el cambio de estado
            kardexRepository.save(kardexEntry); // Guardar el registro en kardex
        });

        // Cálculo de multas por retraso en la devolución si aplica
        if (loan.getDueDate().isBefore(LocalDateTime.now())) { // Si la fecha actual es posterior a la fecha de vencimiento
            long daysLate = (long) Math.ceil((double)
                    Duration.between(loan.getDueDate(), LocalDateTime.now()).toHours() / 24); // Calcular días de retraso
            if (daysLate <= 1) daysLate = 1; // Asegurar al menos un día de retraso
            PenaltyEntity latePenalty = createAndCalculatePenalty("Atraso",
                    (loan.getTotalRental().multiply(BigDecimal.valueOf(daysLate)
                            .setScale(2, RoundingMode.CEILING)))); // Crear multa por atraso
            latePenalty.setLoan(loan); // Asociar la multa al préstamo
            penaltyRepository.save(latePenalty); // Guardar la multa
            loan.setTotalPenalties(loan.getTotalPenalties().add(latePenalty.getPenaltyAmount()
                    .setScale(2, RoundingMode.CEILING))); // Actualizar total de multas del préstamo
        }

        loan.setReturnDate(LocalDateTime.now()); // Establecer la fecha de devolución
        loan.setLoanStatus(getLoanStatusByName("Atrasada")); // Cambiar estado del préstamo
        loanRepository.save(loan); // Guardar el préstamo actualizado
        customer.setUserStatus(getUserStatusByName("Con Deuda"));// Cliente -> "Con Deuda"
        userRepository.save(customer); // Guardar el cliente actualizado
        return toLoanSummaryDTO(loan); // Guardar y retornar el DTO del préstamo actualizado
    }

    // TODO: Proceso de pago de un loan

    // TODO: Dar de baja un loan

    /* Metodos auxiliares */

    public LoanEntity getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan ID: " + loanId));
    }

    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
    }

    public ToolEntity getToolById(Long toolId) {
        return toolRepository.findById(toolId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tool ID: " + toolId));
    }

    public ToolStatusEntity getToolStatusByName(String name) {
        return toolStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tool status name: " + name));
    }

    public LoanStatusEntity getLoanStatusByName(String name) {
        return loanStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan status name: " + name));
    }

    public KardexTypeEntity getKardexTypeByName(String name) {
        return kardexTypeRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid kardex type name: " + name));
    }

    public PenaltyStatusEntity getPenaltyStatusByName(String name) {
        return penaltyStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid penalty status name: " + name));
    }

    public PenaltyTypeEntity getPenaltyTypeByName(String name) {
        return penaltyTypeRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid penalty type name: " + name));
    }

    public UserStatusEntity getUserStatusByName(String name) {
        return userStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user status name: " + name));
    }

    public KardexEntity addKardexEntry(int quantity, ToolEntity tool, KardexTypeEntity type, UserEntity worker) {
        KardexEntity kardexEntry = new KardexEntity();
        kardexEntry.setDateTime(LocalDateTime.now());
        kardexEntry.setQuantity(quantity);
        kardexEntry.setTool(tool);
        kardexEntry.setKardexType(type);
        kardexEntry.setWorkerUser(worker);
        return kardexRepository.save(kardexEntry);
    }

    public PenaltyEntity createAndCalculatePenalty(String penaltyTypeName, BigDecimal value) {
        PenaltyEntity penalty = new PenaltyEntity();
        PenaltyTypeEntity penaltyType = getPenaltyTypeByName(penaltyTypeName);
        penalty.setPenaltyAmount(value.multiply(penaltyType.getPenaltyFactor()
                .setScale(2, RoundingMode.CEILING))); // Calcular el monto de la multa
        penalty.setPenaltyDate(LocalDateTime.now());
        penalty.setDescription("Multa por herramienta en estado: '" + penaltyTypeName.toLowerCase()
                + "'. Por valor de: $" + penalty.getPenaltyAmount().setScale(2, RoundingMode.CEILING));
        penalty.setPenaltyType(penaltyType);
        penalty.setPenaltyStatus(getPenaltyStatusByName("Activo"));
        return penalty;
    };

    public boolean doesLoanBelongToCustomer(LoanEntity loan, Long customerId) {
        return loan.getCustomerUser().getId().equals(customerId);
    }

    /* Metodos Mapper */

    // Convert LoanEntity to LoanSummaryDTO
    public LoanSummaryDTO toLoanSummaryDTO(LoanEntity loan) {
        Objects.requireNonNull(loan, "LoanEntity cannot be null");
        LoanSummaryDTO dto = new LoanSummaryDTO();
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
