// src/main/java/magis/mundi2025/demo/service/BookingService.java
package magis.mundi2025.demo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.model.entity.Booking;
import magis.mundi2025.demo.model.entity.Room;
import magis.mundi2025.demo.model.entity.User;
import magis.mundi2025.demo.repository.BookingRepository;
import magis.mundi2025.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager em; // pentru getReference fără RoomRepository

    public Booking createBookingForRoom(Long roomId) {
        // user hardcod doar pentru demo
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user = userRepository.save(user);

        // referință la room (fără query în DB)
        Room roomRef = em.getReference(Room.class, roomId);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(roomRef);
        booking.setStartDate(LocalDate.now());
        booking.setEndDate(LocalDate.now().plusDays(2));

        return bookingRepository.save(booking);
    }
}
