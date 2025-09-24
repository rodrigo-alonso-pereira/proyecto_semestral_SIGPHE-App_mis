package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.UserSummaryDTO;
import cl.usach.mis.sigpheapp_backend.entities.UserEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserStatusEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserTypeEntity;
import cl.usach.mis.sigpheapp_backend.repositories.UserRepository;
import cl.usach.mis.sigpheapp_backend.repositories.UserStatusRepository;
import cl.usach.mis.sigpheapp_backend.repositories.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired UserRepository userRepository;
    @Autowired UserStatusRepository userStatusRepository;
    @Autowired UserTypeRepository userTypeRepository;

    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDTO)
                .toList();
    }

    public List<UserSummaryDTO> getAllUsersWithDebts() {
        return userRepository.findAllByUserStatusIdEquals(getUserStatusByName("Con Deuda").getId()).stream()
                .map(this::toUserDTO)
                .toList();
    }

    // TODO: Implementar lógica real de elegibilidad
    public boolean isCustomerEligibleForNewLoan(Long userId) {
        return true;
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

    /* Mapper Layer */

    // UserEntity -> UserSummaryDTO
    public UserSummaryDTO toUserDTO(UserEntity user) {
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
