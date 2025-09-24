package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.KardexSummaryDTO;
import cl.usach.mis.sigpheapp_backend.entities.KardexEntity;
import cl.usach.mis.sigpheapp_backend.repositories.KardexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class KardexService {

    @Autowired KardexRepository kardexRepository;

    public List<KardexSummaryDTO> getAllKardexEntries() {
        return kardexRepository.findAll().stream()
                .map(this::toKardexDTO)
                .toList();
    }

    /* Mapper Layer */
    private KardexSummaryDTO toKardexDTO(KardexEntity kardex) {
        Objects.requireNonNull(kardex, "KardexEntity cannot be null");
        KardexSummaryDTO dto = new KardexSummaryDTO();
        dto.setRegistrationDate(kardex.getDateTime());
        dto.setQuantity(kardex.getQuantity());
        dto.setToolName(kardex.getTool().getName());
        dto.setKardexTypeName(kardex.getKardexType().getName());
        dto.setWorkerName(kardex.getWorkerUser().getName());
        return dto;
    }
}
