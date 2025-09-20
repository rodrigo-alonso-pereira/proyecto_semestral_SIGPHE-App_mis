package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.CreateLoanRequestDTO;
import cl.usach.mis.sigpheapp_backend.dtos.LoanStatusDTO;
import cl.usach.mis.sigpheapp_backend.dtos.LoanSummaryDTO;
import cl.usach.mis.sigpheapp_backend.entities.*;
import cl.usach.mis.sigpheapp_backend.repositories.LoanRepository;
import cl.usach.mis.sigpheapp_backend.repositories.LoanStatusRepository;
import cl.usach.mis.sigpheapp_backend.repositories.ToolRepository;
import cl.usach.mis.sigpheapp_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        UserEntity customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        // Validar que el cliente sea elegible para un nuevo préstamo
        if (!userService.isCustomerEligibleForNewLoan(customer.getId())) {
            throw new IllegalStateException("Customer " + customer.getName() + " is not eligible for a new loan");
        }

        LoanStatusEntity activeStatus = loanStatusRepository.findByName("Vigente")
                .orElseThrow(() -> new IllegalStateException("Active loan status not found"));

        LoanEntity newLoan = new LoanEntity();
        newLoan.setCustomerUser(customer);
        newLoan.setStartDate(LocalDateTime.now());
        newLoan.setDueDate(dto.getDueDate());
        newLoan.setLoanStatus(activeStatus);
        newLoan.setTotalAmount(toolService.getTotalAmount(dto.getToolIds()));

        for (Long toolId : dto.getToolIds()) {
            // Validar que la herramienta esté disponible
            if (!toolService.isToolAvailable(toolId)) {
                throw new IllegalStateException("Tool with ID " + toolId + " is not available");
            }
            // Obtener la herramienta
            ToolEntity tool = toolRepository.findById(toolId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid tool ID: " + toolId));

            LoanDetailEntity loanDetail = new LoanDetailEntity(); // Crear el detalle del préstamo
            LoanDetailEntityId detailId = new LoanDetailEntityId(); // Crear ID compuesto
            detailId.setToolId(toolId); // Asignar toolId al ID compuesto
            loanDetail.setId(detailId); // Asignar el ID compuesto al detalle
            loanDetail.setRentalValueAtTime(tool.getRentalValue()); // Valor de alquiler al momento del préstamo

            newLoan.addLoanDetail(loanDetail); // Asociar el detalle al préstamo
        }
        LoanEntity savedLoan = loanRepository.save(newLoan); // Guardar el préstamo junto con sus detalles
        return toLoanSummaryDTO(savedLoan); // Convertir y retornar el DTO del préstamo creado
        // TODO: Validar que las herramientas cambiaron su estado a "Prestada" con un trigger en la base de datos
    }

    /* Mapper Layer */

    // Convert LoanEntity to LoanSummaryDTO
    public LoanSummaryDTO toLoanSummaryDTO(LoanEntity loan) {
        Objects.requireNonNull(loan, "LoanEntity cannot be null");
        LoanSummaryDTO dto = new LoanSummaryDTO();
        dto.setId(loan.getId());
        dto.setStartDate(loan.getStartDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setDueDate(loan.getDueDate());
        dto.setTotalAmount(loan.getTotalAmount());
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
