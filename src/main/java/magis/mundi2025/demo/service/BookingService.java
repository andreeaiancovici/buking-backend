package magis.mundi2025.demo.service;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.model.entity.Booking;
import magis.mundi2025.demo.model.entity.BookingStatus;
import magis.mundi2025.demo.model.entity.Room;
import magis.mundi2025.demo.model.entity.User;
import magis.mundi2025.demo.repository.BookingRepository;
import magis.mundi2025.demo.repository.RoomRepository;
import magis.mundi2025.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));
    }

    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));
    }

    public boolean isRoomAvailable(
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate) {

        return !bookingRepository.existsOverlappingBooking(
                roomId,
                BookingStatus.RESERVED,
                checkInDate,
                checkOutDate
        );
    }

    @Transactional
    public Booking createBooking(
            String name,
            String email,
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer numberOfGuests) {

        if (name == null || name.isBlank()) {
            throw new RuntimeException("Name is required");
        }

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (checkInDate == null || checkOutDate == null) {
            throw new RuntimeException(
                    "Check-in and check-out dates are required"
            );
        }

        if (checkInDate.isBefore(LocalDate.now())) {
            throw new RuntimeException(
                    "Check-in date cannot be in the past"
            );
        }

        if (!checkInDate.isBefore(checkOutDate)) {
            throw new RuntimeException(
                    "Check-out date must be after check-in date"
            );
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        if (numberOfGuests == null ||
                numberOfGuests < 1 ||
                numberOfGuests > room.getCapacity()) {

            throw new RuntimeException(
                    "Invalid number of guests"
            );
        }

        if (!isRoomAvailable(
                roomId,
                checkInDate,
                checkOutDate)) {

            throw new RuntimeException(
                    "Room is already reserved for these dates"
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        User user =
                userRepository
                        .findByEmailIgnoreCase(normalizedEmail)
                        .orElseGet(() -> {

                            User newUser = new User();

                            newUser.setName(name.trim());
                            newUser.setEmail(normalizedEmail);

                            return userRepository.save(newUser);
                        });

        long numberOfNights =
                ChronoUnit.DAYS.between(
                        checkInDate,
                        checkOutDate
                );

        BigDecimal totalPrice =
                room.getPricePerNight()
                        .multiply(
                                BigDecimal.valueOf(numberOfNights)
                        );

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setRoom(room);

        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);

        booking.setNumberOfGuests(numberOfGuests);

        booking.setTotalPrice(totalPrice);

        booking.setStatus(
                BookingStatus.RESERVED
        );

        return bookingRepository.save(booking);
    }
}