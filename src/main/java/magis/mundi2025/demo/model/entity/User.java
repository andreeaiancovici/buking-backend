package magis.mundi2025.demo.model.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;

    @OneToMany(mappedBy = "user") // <-- Corectează: Bookings are un câmp numit "user"
    private List<Bookings> bookings;

}
