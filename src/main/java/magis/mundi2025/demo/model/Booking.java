package magis.mundi2025.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // camera rezervata (un Room poate avea mai multe rezervari, pe date diferite)
    @ManyToOne
    private Room room;

    private LocalDate checkIn;
    private LocalDate checkOut;
    private String guestName;
    private Integer guests;
}