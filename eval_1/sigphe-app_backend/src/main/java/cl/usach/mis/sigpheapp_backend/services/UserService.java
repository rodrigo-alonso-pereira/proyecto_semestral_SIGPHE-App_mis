package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.dtos.UserSummaryDTO;
import cl.usach.mis.sigpheapp_backend.entities.UserEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserStatusEntity;
import cl.usach.mis.sigpheapp_backend.entities.UserTypeEntity;
import cl.usach.mis.sigpheapp_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired UserRepository userRepository;

    public UserSummaryDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toUserDTO)
                .orElse(null);
    }

    // TODO: Implementar lógica real de elegibilidad
    public boolean isCustomerEligibleForNewLoan(Long userId) {
        return true;
    }

    /* Mapper Layer */

    // UserEntity -> UserSummaryDTO
    public UserSummaryDTO toUserDTO(UserEntity user) {
        Objects.requireNonNull(user, "UserEntity cannot be null");
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setNationalId(user.getNationalId());
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
