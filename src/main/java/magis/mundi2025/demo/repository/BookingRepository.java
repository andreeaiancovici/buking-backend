package magis.mundi2025.demo.repository;

import magis.mundi2025.demo.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Logica magică pentru suprapunere de date
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.id = :roomId AND b.status = 'CONFIRMED' AND (b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)")
    int countOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}