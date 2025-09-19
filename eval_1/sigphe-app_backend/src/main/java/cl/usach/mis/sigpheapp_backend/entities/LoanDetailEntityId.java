package cl.usach.mis.sigpheapp_backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@EqualsAndHashCode
public class LoanDetailEntityId implements Serializable {
    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "tool_id")
    private Long toolId;
}
