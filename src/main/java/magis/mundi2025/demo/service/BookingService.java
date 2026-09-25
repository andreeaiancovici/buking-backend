package magis.mundi2025.demo.service;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.converter.BookingConverter;
import magis.mundi2025.demo.model.dto.BookingDTO;
import magis.mundi2025.demo.model.entity.Booking;
import magis.mundi2025.demo.model.entity.Room;
import magis.mundi2025.demo.model.entity.User;
import magis.mundi2025.demo.repository.BookingRepository;
import magis.mundi2025.demo.repository.RoomRepository;
import magis.mundi2025.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingConverter bookingConverter;

    public BookingDTO createBooking(Long userId, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit!"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Camera nu a fost găsită!"));

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setTotalPrice(room.getPricePerNight().multiply(BigDecimal.valueOf(nights)));
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepository.save(booking);
        return bookingConverter.toDTO(savedBooking);
    }

    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rezervarea nu a fost găsită!"));
        return bookingConverter.toDTO(booking);
    }
}