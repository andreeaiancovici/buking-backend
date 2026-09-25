package magis.mundi2025.demo.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    // Lista de rezervari ale user-ului (asemanator cu lista de camere de la Property)
    private List<BookingDTO> bookings;
}