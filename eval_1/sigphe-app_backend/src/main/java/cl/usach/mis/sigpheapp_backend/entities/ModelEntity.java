package cl.usach.mis.sigpheapp_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "models")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ModelEntity {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Column(nullable = false)
    private boolean status = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private BrandEntity brand;

    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ToolEntity> tools;

    // Helper method to add a tool
    public void addTool(ToolEntity tool) {
        if (this.tools == null) {
            this.tools = new java.util.ArrayList<>();
        }
        this.tools.add(tool);
        tool.setModel(this);
    }
}
