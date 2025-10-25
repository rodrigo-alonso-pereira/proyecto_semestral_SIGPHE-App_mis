package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.ClientsWithDebtsDTO;
import cl.usach.mis.sigpheapp_backend.dtos.UserSummaryDTO;
import cl.usach.mis.sigpheapp_backend.entities.UserEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserStatusEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserTypeEntity;
import cl.usach.mis.sigpheapp_backend.exceptions.ResourceNotFoundException;
import cl.usach.mis.sigpheapp_backend.repositories.UserRepository;
import cl.usach.mis.sigpheapp_backend.repositories.UserStatusRepository;
import cl.usach.mis.sigpheapp_backend.repositories.UserTypeRepository;
import cl.usach.mis.sigpheapp_backend.repositories.projection.ClientsWithDebtsProjection;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    // Constantes de estados de usuario
    private static final String STATUS_USER_WITH_DEBT = "Con Deuda";
    private static final String STATUS_USER_ACTIVE = "Activo";

    // Constantes de estados de prestamo
    private static final String STATUS_LOAN_ACTIVE = "Vigente";
    private static final String STATUS_LOAN_OVERDUE = "Atrasada";

    private static final String TYPE_USER_COSTUMER = "Cliente";
    private static final String TYPE_USER_WORKER = "Trabajador";

    @Autowired UserRepository userRepository;
    @Autowired UserStatusRepository userStatusRepository;
    @Autowired UserTypeRepository userTypeRepository;

    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDTO)
                .toList();
    }

    public List<UserSummaryDTO> getAllCostumers() {
        return userRepository.findAllByUserTypeIdEquals(getUserTypeByName(TYPE_USER_COSTUMER).getId()).stream()
                .map(this::toUserDTO)
                .toList();
    }

    public List<UserSummaryDTO> getActiveCostumers() {
        return userRepository.findAllByUserTypeIdEqualsAndUserStatusIdEquals(
                getUserTypeByName(TYPE_USER_COSTUMER).getId(), getUserStatusByName(STATUS_USER_ACTIVE).getId()).stream()
                .map(this::toUserDTO)
                .toList();
    }

    public List<UserSummaryDTO> getAllEmployees() {
        return userRepository.findAllByUserTypeIdEquals(getUserTypeByName(TYPE_USER_WORKER).getId()).stream()
                .map(this::toUserDTO)
                .toList();
    }

    public List<UserSummaryDTO> getAllUsersWithDebts() {
        return userRepository.findAllByUserStatusIdEquals(getUserStatusByName(STATUS_USER_WITH_DEBT).getId()).stream()
                .map(this::toUserDTO)
                .toList();
    }

    /**
     * Obtiene todos los usuarios con deudas en un rango de fechas.
     *
     * @param startDate Fecha de inicio del rango.
     * @param endDate   Fecha de fin del rango.
     * @return Lista de usuarios con deudas en el rango de fechas.
     */
    public List<ClientsWithDebtsDTO> getAllUsersWithDebtsByDateRange(@NotNull LocalDateTime startDate,
                                                                     @NotNull LocalDateTime endDate) {
        List<ClientsWithDebtsDTO> usersWithDebts = new ArrayList<>();
        for (ClientsWithDebtsProjection projection : userRepository.findAllUserWithDebtsBetweenDates(
                startDate,
                endDate,
                STATUS_LOAN_OVERDUE,
                STATUS_LOAN_ACTIVE)) {
            usersWithDebts.add(toClientsWithDebtsDTO(projection));
        }
        return usersWithDebts;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCostumerStatus(UserEntity customer) {
        customer.setUserStatus(getUserStatusByName(STATUS_USER_WITH_DEBT));
        userRepository.save(customer);
    }

    /* Metodos auxiliares */

    private UserStatusEntity getUserStatusByName(String name) {
        return userStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("User Status", "name", name));
    }

    private UserTypeEntity getUserTypeByName(String name) {
        return userTypeRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("User Type", "name", name));
    }

    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /* Mapper Layer */

    // UserEntity -> UserSummaryDTO
    private UserSummaryDTO toUserDTO(UserEntity user) {
        Objects.requireNonNull(user, "UserEntity cannot be null");
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setUserStatus(Optional.ofNullable(user.getUserStatus())
                .map(UserStatusEntity::getName)
                .orElse("Unknown"));
        dto.setUserType(Optional.ofNullable(user.getUserType())
                .map(UserTypeEntity::getName)
                .orElse("Unknown"));
        return dto;
    }
}
