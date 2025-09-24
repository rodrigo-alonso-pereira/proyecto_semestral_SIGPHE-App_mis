package cl.usach.mis.sigpheapp_backend.controllers;

import cl.usach.mis.sigpheapp_backend.dtos.KardexSummaryDTO;
import cl.usach.mis.sigpheapp_backend.services.KardexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kardex")
public class KardexController {

    @Autowired KardexService kardexService;

    @GetMapping
    public ResponseEntity<List<KardexSummaryDTO>> getAll() {
        List<KardexSummaryDTO> kardexEntries = kardexService.getAllKardexEntries();
        return ResponseEntity.ok(kardexEntries);
    }
}
