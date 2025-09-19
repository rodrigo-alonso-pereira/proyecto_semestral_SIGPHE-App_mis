package cl.usach.mis.sigpheapp_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(name = "national_id", nullable = false, unique = true, length = 30)
    private String nationalId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_status_id", nullable = false)
    private UserStatusEntity userStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id", nullable = false)
    private UserTypeEntity userType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserPhoneEntity> phones;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<KardexEntity> kardexRecords;

    @OneToMany(mappedBy = "customerUser", fetch = FetchType.LAZY)
    private List<LoanEntity> loans;

    // Helper method to add a phone
    // This ensures the bidirectional relationship is maintained
    public void addPhone(UserPhoneEntity phone) {
        if (this.phones == null) {
            this.phones = new java.util.ArrayList<>();
        }
        this.phones.add(phone);
        phone.setUser(this);
    }

    // Helper method to add a kardex record
    public void addKardexRecord(KardexEntity kardex) {
        if (this.kardexRecords == null) {
            this.kardexRecords = new java.util.ArrayList<>();
        }
        this.kardexRecords.add(kardex);
        kardex.setUser(this);
    }

    // Helper method to add a loan
    public void addLoan(LoanEntity loan) {
        if (this.loans == null) {
            this.loans = new java.util.ArrayList<>();
        }
        this.loans.add(loan);
        loan.setCustomerUser(this);
    }
}

