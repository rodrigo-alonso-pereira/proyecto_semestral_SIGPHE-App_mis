package cl.usach.mis.sigpheapp_backend.controllers;

import cl.usach.mis.sigpheapp_backend.dtos.*;
import cl.usach.mis.sigpheapp_backend.services.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@CrossOrigin("*")
public class LoanController {

    // Constantes de estado de préstamo
    private static final String STATUS_LOAN_ACTIVE = "Vigente";
    private static final String STATUS_LOAN_OVERDUE = "Atrasada";

    @Autowired private LoanService loanService;

    @GetMapping
    public ResponseEntity<List<LoanDTO>> getAll() {
        List<LoanDTO> loans = loanService.getAllLoansSummary();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/active")
    public ResponseEntity<List<LoanDTO>> getActiveLoans() {
        List<String> statuses = Arrays.asList(STATUS_LOAN_ACTIVE, STATUS_LOAN_OVERDUE);
        List<LoanDTO> loans = loanService.getAllLoansByStatuses(statuses);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/active/date-range")
    public ResponseEntity<List<LoanDTO>> getAllLoansByStatusesAndDateRange(
            @Valid @RequestBody DateRangeRequestDTO request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        List<String> statuses = Arrays.asList(STATUS_LOAN_ACTIVE, STATUS_LOAN_OVERDUE);
        List<LoanDTO> loans = loanService.getAllLoansByStatusesAndDateRange(statuses,
                request.getStartDate(), request.getEndDate());
        return ResponseEntity.ok(loans);
    }

    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@Valid @RequestBody CreateLoanRequestDTO request) {
        LoanDTO createdLoan = loanService.createLoan(request);
        URI location = ServletUriComponentsBuilder // Retornar 201 Created con la ubicación del nuevo recurso
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdLoan.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdLoan);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDTO> returnLoan(@PathVariable @NotNull Long id,
                                              @Valid @RequestBody ReturnLoanRequestDTO request) {
        LoanDTO updatedLoan = loanService.processReturnLoan(id, request);
        return ResponseEntity.ok(updatedLoan);
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<LoanDTO> makePayment(@PathVariable @NotNull Long id,
                                               @Valid @RequestBody PaymentLoanRequestDTO request) {
        LoanDTO updatedLoan = loanService.processPayment(id, request);
        return ResponseEntity.ok(updatedLoan);
    }
}
