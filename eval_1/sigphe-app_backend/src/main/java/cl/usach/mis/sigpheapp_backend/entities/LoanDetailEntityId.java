package cl.usach.mis.sigpheapp_backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LoanDetailEntityId implements Serializable {
    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "tool_id")
    private Long toolId;
}
