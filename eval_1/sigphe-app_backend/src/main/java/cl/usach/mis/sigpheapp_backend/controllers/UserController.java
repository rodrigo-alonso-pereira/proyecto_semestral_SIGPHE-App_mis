package cl.usach.mis.sigpheapp_backend.controllers;

import cl.usach.mis.sigpheapp_backend.dtos.UserSummaryDTO;
import cl.usach.mis.sigpheapp_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired UserService userService;

    @GetMapping
    public ResponseEntity<List<UserSummaryDTO>> getAll() {
        List<UserSummaryDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/with-debts")
    public ResponseEntity<List<UserSummaryDTO>> getUsersWithDebts() {
        List<UserSummaryDTO> users = userService.getAllUsersWithDebts();
        return ResponseEntity.ok(users);
    }
}
