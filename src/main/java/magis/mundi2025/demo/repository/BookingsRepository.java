package magis.mundi2025.demo.repository;
import magis.mundi2025.demo.model.entity.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingsRepository extends JpaRepository<Bookings, Long> {
}
