package cl.usach.mis.sigpheapp_backend.controllers;

import cl.usach.mis.sigpheapp_backend.dtos.DateRangeRequestDTO;
import cl.usach.mis.sigpheapp_backend.dtos.UserSummaryDTO;
import cl.usach.mis.sigpheapp_backend.repositories.projection.ClientsWithDebtsProjection;
import cl.usach.mis.sigpheapp_backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin("*")
public class UserController {

    @Autowired UserService userService;

    @GetMapping
    public ResponseEntity<List<UserSummaryDTO>> getAll() {
        List<UserSummaryDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/costumers")
    public ResponseEntity<List<UserSummaryDTO>> getAllCostumers() {
        List<UserSummaryDTO> users = userService.getAllCostumers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/costumers/active")
    public ResponseEntity<List<UserSummaryDTO>> getActiveCostumers() {
        List<UserSummaryDTO> users = userService.getActiveCostumers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserSummaryDTO>> getAllEmployees() {
        List<UserSummaryDTO> users = userService.getAllEmployees();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/with-debts")
    public ResponseEntity<List<UserSummaryDTO>> getUsersWithDebts() {
        List<UserSummaryDTO> users = userService.getAllUsersWithDebts();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/with-debts/date-range")
    public ResponseEntity<List<ClientsWithDebtsProjection>> getUsersWithDebts(
            @Valid @RequestBody DateRangeRequestDTO request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        List<ClientsWithDebtsProjection> users = userService.getAllUsersWithDebtsByDateRange(request.getStartDate(),
                request.getEndDate());
        return ResponseEntity.ok(users);
    }
}
