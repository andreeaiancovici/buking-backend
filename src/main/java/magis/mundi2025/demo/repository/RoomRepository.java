package magis.mundi2025.demo.repository;

import magis.mundi2025.demo.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("""
        select r from Room r
        where r.property.id = :propertyId
          and r.capacity >= :guests
          and r.id not in (select b.room.id from Booking b
                           where b.checkIn < :checkOut and b.checkOut > :checkIn)
        """)
    List<Room> findAvailable(@Param("propertyId") Long propertyId,
                             @Param("checkIn") LocalDate checkIn,
                             @Param("checkOut") LocalDate checkOut,
                             @Param("guests") int guests);
}