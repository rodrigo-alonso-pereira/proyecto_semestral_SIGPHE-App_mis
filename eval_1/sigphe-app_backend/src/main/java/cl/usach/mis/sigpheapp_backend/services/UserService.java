package cl.usach.mis.sigpheapp_backend.services;

import cl.usach.mis.sigpheapp_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired UserRepository userRepository;

    // TODO: Implementar lógica real de elegibilidad
    public boolean isCustomerEligibleForNewLoan(Long userId) {
        return true;
    }

    /* Mapper Layer */

    // UserEntity -> UserSummaryDTO
    /*public UserSummaryDTO toUserDTO(UserEntity user) {
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
    }*/
}
