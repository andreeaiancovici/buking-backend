package magis.mundi2025.demo.repository;

import magis.mundi2025.demo.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
List<Booking> findByGuestNameContainingIgnoreCaseOrderByCheckInDesc(String guestName);}