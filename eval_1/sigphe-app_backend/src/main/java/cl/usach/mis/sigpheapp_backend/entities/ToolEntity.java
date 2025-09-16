package cl.usach.mis.sigpheapp_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "tools")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal replacementValue;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalValue;

    private boolean status = true;

    @Column(nullable = false)
    private ToolCategoryEntity toolCategory;

    @Column(nullable = false)
    private ToolStatusEntity toolStatus;

    @Column(nullable = false)
    private ModelEntity model;
}

