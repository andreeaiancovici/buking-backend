package magis.mundi2025.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "users") // pentru că "user" este rezervat în SQL
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;
}
