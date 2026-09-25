package magis.mundi2025.demo.converter;

import magis.mundi2025.demo.model.dto.UserDTO;
import magis.mundi2025.demo.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserDTO convertToDTO(User user) {

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        return dto;
    }
}