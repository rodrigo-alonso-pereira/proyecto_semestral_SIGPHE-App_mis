package cl.usach.mis.sigpheapp_backend.controllers;

import cl.usach.mis.sigpheapp_backend.dtos.CreateLoanRequestDTO;
import cl.usach.mis.sigpheapp_backend.dtos.LoanSummaryDTO;
import cl.usach.mis.sigpheapp_backend.dtos.ReturnLoanRequestDTO;
import cl.usach.mis.sigpheapp_backend.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    @Autowired private LoanService loanService;

    @GetMapping
    public ResponseEntity<List<LoanSummaryDTO>> getAll() {
        List<LoanSummaryDTO> loans = loanService.getAllLoansSummary();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/active")
    public ResponseEntity<List<LoanSummaryDTO>> getActiveLoans() {
        List<String> statuses = Arrays.asList("Vigente", "Atrasada");
        List<LoanSummaryDTO> loans = loanService.getAllLoansByStatuses(statuses);
        return ResponseEntity.ok(loans);
    }

    @PostMapping
    public ResponseEntity<LoanSummaryDTO> createLoan(@RequestBody CreateLoanRequestDTO request) {
        LoanSummaryDTO createdLoan = loanService.createLoan(request);
        URI location = ServletUriComponentsBuilder // Retornar 201 Created con la ubicación del nuevo recurso
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdLoan.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdLoan);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<LoanSummaryDTO> returnLoan(@PathVariable Long id, @RequestBody ReturnLoanRequestDTO request) {
        LoanSummaryDTO updatedLoan = loanService.processReturnLoan(id, request);
        return ResponseEntity.ok(updatedLoan);
    }
}
