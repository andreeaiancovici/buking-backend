package magis.mundi2025.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users") // Folosim "users" pentru că "user" poate fi cuvânt rezervat în SQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role; // "CLIENT" sau "ADMIN"

    // Un user poate avea mai multe rezervări
    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;
}