package magis.mundi2025.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    private String status; // Ex: "CONFIRMED", "PENDING", "CANCELLED"

    // Multe rezervări aparțin unui singur User
    @ManyToOne
    @JoinColumn(name = "user_id") // Numele coloanei de legătură din baza de date
    private User user;

    // Multe rezervări se pot face pe aceeași Cameră (în perioade diferite)
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}