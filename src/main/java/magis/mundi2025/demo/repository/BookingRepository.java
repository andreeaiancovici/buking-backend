package magis.mundi2025.demo.repository;

import magis.mundi2025.demo.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
