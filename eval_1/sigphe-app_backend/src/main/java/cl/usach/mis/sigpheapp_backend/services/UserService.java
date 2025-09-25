package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.DateRangeRequestDTO;
import cl.usach.mis.sigpheapp_backend.dtos.UserSummaryDTO;
import cl.usach.mis.sigpheapp_backend.entities.UserEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserStatusEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserTypeEntity;
import cl.usach.mis.sigpheapp_backend.repositories.UserRepository;
import cl.usach.mis.sigpheapp_backend.repositories.UserStatusRepository;
import cl.usach.mis.sigpheapp_backend.repositories.UserTypeRepository;
import cl.usach.mis.sigpheapp_backend.repositories.projection.ClientsWithDebtsProjection;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    // Constantes de estados de usuario
    private static final String STATUS_USER_WITH_DEBT = "Con Deuda";

    // Constantes de estados de prestamo
    private static final String STATUS_LOAN_ACTIVE = "Vigente";
    private static final String STATUS_LOAN_OVERDUE = "Atrasada";

    @Autowired UserRepository userRepository;
    @Autowired UserStatusRepository userStatusRepository;
    @Autowired UserTypeRepository userTypeRepository;

    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDTO)
                .toList();
    }

    public List<UserSummaryDTO> getAllUsersWithDebts() {
        return userRepository.findAllByUserStatusIdEquals(getUserStatusByName(STATUS_USER_WITH_DEBT).getId()).stream()
                .map(this::toUserDTO)
                .toList();
    }

    public List<ClientsWithDebtsProjection> getAllUsersWithDebtsByDateRange(@NotNull LocalDateTime startDate,
                                                                            @NotNull LocalDateTime endDate) {
        return userRepository.findAllUserWithDebtsBetweenDates(startDate, endDate,
                        STATUS_LOAN_OVERDUE, STATUS_LOAN_ACTIVE);
    }

    /* Metodos auxiliares */

    private UserStatusEntity getUserStatusByName(String name) {
        return userStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User Status name: " + name));
    }

    private UserTypeEntity getUserTypeByName(String name) {
        return userTypeRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User Type name: " + name));
    }

    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
    }

    /* Mapper Layer */

    // UserEntity -> UserSummaryDTO
    private UserSummaryDTO toUserDTO(UserEntity user) {
        Objects.requireNonNull(user, "UserEntity cannot be null");
        UserSummaryDTO dto = new UserSummaryDTO();
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
