package cl.usach.mis.sigpheapp_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientsWithDebtsDTO {
    private String userName;
    private String userEmail;
    private String userStatus;
    private String userType;
    private Long totalOverdueLoans;
}
