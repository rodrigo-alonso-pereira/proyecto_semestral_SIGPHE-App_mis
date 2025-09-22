package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.CreateLoanRequestDTO;
import cl.usach.mis.sigpheapp_backend.dtos.LoanSummaryDTO;
import cl.usach.mis.sigpheapp_backend.entities.*;
import cl.usach.mis.sigpheapp_backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired private UserService userService;
    @Autowired private ToolService toolService;
    @Autowired private LoanRepository loanRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LoanStatusRepository loanStatusRepository;
    @Autowired private ToolRepository toolRepository;
    @Autowired private ToolStatusRepository toolStatusRepository;
    @Autowired private KardexTypeRepository kardexTypeRepository;
    @Autowired private KardexRepository kardexRepository;

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
        // Validar que trabajador exista
        UserEntity worker = userRepository.findById(dto.getWorkerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid worker ID: " + dto.getWorkerId()));

        // Validar que el cliente exista
        UserEntity customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID: " + dto.getCustomerId()));

        // Validar que el cliente sea elegible para un nuevo préstamo según reglas de negocio
        if (!userService.isCustomerEligibleForNewLoan(customer.getId())) {
            throw new IllegalStateException("Customer " + customer.getName() + " is not eligible for a new loan");
        }

        // Obtener entidades necesarias para el préstamo
        LoanStatusEntity loanStatus = loanStatusRepository.findByName("Vigente")
                .orElseThrow(() -> new IllegalStateException("Loan status 'Vigente' not found"));

        ToolStatusEntity toolStatus = toolStatusRepository.findByName("Prestada")
                .orElseThrow(() -> new IllegalStateException("Tool status 'Prestada' not found"));

        KardexTypeEntity kardexType = kardexTypeRepository.findByName("Prestamo")
                .orElseThrow(() -> new IllegalStateException("Kardex type 'Prestamo' not found"));

        // Preparacion para calcular el valor total del alquiler
        BigDecimal totalRental = BigDecimal.ZERO;
        long rentalDays = (long) Math.ceil((double) Duration.between(LocalDateTime.now(), dto.getDueDate()).toHours() / 24); // Calcular días de alquiler redondeando hacia arriba
        if (rentalDays <= 1) rentalDays = 1; // Asegurar al menos un día de alquiler
        for (Long toolId : dto.getToolIds()) {
            // Obtener la herramienta
            ToolEntity tool = toolRepository.findById(toolId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid tool ID: " + toolId));

            // Validar que la herramienta esté disponible
            if (!tool.getToolStatus().getName().equals("Disponible")) {
                throw new IllegalStateException("Tool with ID " + toolId + " is not available");
            }

            // Calcular el valor del alquiler de herramienta actual
            BigDecimal rentalValue = tool.getRentalValue(); // Obtener valor de alquiler
            totalRental = totalRental.add(rentalValue.multiply(BigDecimal.valueOf(rentalDays))); // Sumar al total
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
            KardexEntity kardexEntry = new KardexEntity();
            kardexEntry.setDateTime(LocalDateTime.now());
            kardexEntry.setQuantity(-1); // Cantidad negativa por préstamo
            kardexEntry.setTool(tool);
            kardexEntry.setKardexType(kardexType);
            kardexEntry.setWorkerUser(worker); // Usuario que realiza el préstamo
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

    // TODO: Logica para devolucion de herramientas, cambiar estado de herramienta
    // segun Si esta en reparacion, dada de baja o pasa a disponible.

    /* Mapper Layer */

    // Convert LoanEntity to LoanSummaryDTO
    public LoanSummaryDTO toLoanSummaryDTO(LoanEntity loan) {
        Objects.requireNonNull(loan, "LoanEntity cannot be null");
        LoanSummaryDTO dto = new LoanSummaryDTO();
        dto.setId(loan.getId());
        dto.setStartDate(loan.getStartDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setDueDate(loan.getDueDate());
        dto.setTotalAmount(loan.getTotalRental());
        // loanStatus and customerName might be null, handle accordingly
        dto.setLoanStatus(Optional.ofNullable(loan.getLoanStatus())
                .map(LoanStatusEntity::getName)
                .orElse("Unknown"));
        dto.setCustomerName(Optional.ofNullable(loan.getCustomerUser())
                .map(UserEntity::getName)
                .orElse("Unknown"));
        return dto;
    }
}
