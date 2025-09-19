package cl.usach.mis.sigpheapp_backend.controllers;

import cl.usach.mis.sigpheapp_backend.dtos.LoanSummaryDTO;
import cl.usach.mis.sigpheapp_backend.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
