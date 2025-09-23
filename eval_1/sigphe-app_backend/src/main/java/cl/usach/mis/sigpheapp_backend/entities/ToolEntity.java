package cl.usach.mis.sigpheapp_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tools")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ToolEntity {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "replacement_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal replacementValue;

    @Column(name = "rental_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_category_id", nullable = false)
    private ToolCategoryEntity toolCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tool_status_id", nullable = false)
    private ToolStatusEntity toolStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelEntity model;

    @OneToMany(mappedBy = "tool", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LoanDetailEntity> loanDetails;

    // Metodo helpter para agregar LoanDetail y mantener la relacion bidireccional
    public void addLoanDetail(LoanDetailEntity loanDetail) {
        if (this.loanDetails == null) {
            this.loanDetails = new java.util.ArrayList<>();
        }
        this.loanDetails.add(loanDetail);
        loanDetail.setTool(this);
    }
}