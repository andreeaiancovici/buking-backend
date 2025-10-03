package magis.mundi2025.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Bookings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="property_id")
    private Property property;

    @ManyToOne // <-- CORECTAT: O rezervare are O cameră (conform SQL-ului tău: room_id)
    @JoinColumn(name="room_id")
    private Room room; // <-- CORECTAT: schimbat din List<Room> rooms în Room room

    private String checkIn;
    private String checkOut;
    private Double price;

}
