package cl.usach.mis.sigpheapp_backend.repositories;

import cl.usach.mis.sigpheapp_backend.entities.ToolEntity;
import cl.usach.mis.sigpheapp_backend.repositories.projection.MostUsedToolProjection;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {
    @Query(value = "select " +
            "   t.id as toolId," +
            "   t.name as toolName, " +
            "   m.name as toolModel, " +
            "   b.name as toolBrand, " +
            "   count(ld.tool_id) as usageCount " +
            "from sigphe.loan_details ld " +
            "join sigphe.tools t on ld.tool_id = t.id " +
            "join sigphe.models m on t.model_id = m.id " +
            "join sigphe.brands b on m.brand_id = b.id  " +
            "group by ld.tool_id, t.id, m.id, b.id " +
            "order by usageCount desc " +
            "limit 3",
            nativeQuery = true)
    List<MostUsedToolProjection> findMostUsedTools();

    @Query(value = "select " +
            "   t.id as toolId," +
            "   t.name as toolName, " +
            "   m.name as toolModel, " +
            "   b.name as toolBrand, " +
            "   count(ld.tool_id) as usageCount " +
            "from sigphe.loan_details ld " +
            "join sigphe.tools t on ld.tool_id = t.id " +
            "join sigphe.models m on t.model_id = m.id " +
            "join sigphe.brands b on m.brand_id = b.id  " +
            "join sigphe.loans l on ld.loan_id = l.id " +
            "where l.start_date between :startDate and :endDate " +
            "group by ld.tool_id, t.id, m.id, b.id " +
            "order by usageCount desc " +
            "limit 3",
            nativeQuery = true)
    List<MostUsedToolProjection> findMostUsedToolsBetweenDates(@NotNull LocalDateTime startDate,
                                                               @NotNull LocalDateTime endDate);

    List<ToolEntity> findAllByToolStatusIdEquals(@NotNull Long toolStatusId);
}
