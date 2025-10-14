package cl.usach.mis.sigpheapp_backend.repositories.projection;

public interface ClientsWithDebtsProjection {
    String getUserName();
    String getUserEmail();
    String getUserStatus();
    String getUserType();
    Long getLoanId();
    String getStatusLoan();
    String getDueDate();
}