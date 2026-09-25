package magis.mundi2025.demo.converter;

import magis.mundi2025.demo.model.dto.UserDTO;
import magis.mundi2025.demo.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserDTO toDTO(User entity) {
        if (entity == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setRole(entity.getRole());

        // Nu trimitem parola în DTO!
        return dto;
    }
}