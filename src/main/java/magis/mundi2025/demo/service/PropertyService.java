package magis.mundi2025.demo.service;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.model.entity.Booking;
import magis.mundi2025.demo.model.entity.Property;
import magis.mundi2025.demo.model.entity.Room;
import magis.mundi2025.demo.repository.BookingRepository;
import magis.mundi2025.demo.repository.PropertyRepository;
import magis.mundi2025.demo.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

    public List<Property> search(String q) {
        return propertyRepository
                .findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(q, q);
    }

    public List<Room> findAvailableRooms(Long propertyId, LocalDate in, LocalDate out, int guests) {
        return roomRepository.findAvailable(propertyId, in, out, guests);
    }

    public int countAvailable(Long propertyId, LocalDate in, LocalDate out, int guests) {
        return findAvailableRooms(propertyId, in, out, guests).size();
    }

    // NOU: camerele ocupate pe acele date, indiferent de capacitate
    public Set<Long> findBookedRoomIds(Long propertyId, LocalDate in, LocalDate out) {
        return new HashSet<>(roomRepository.findBookedRoomIds(propertyId, in, out));
    }

    @Transactional
    public void book(Long roomId, LocalDate in, LocalDate out, String guestName, int guests) {
        // NOU: findByIdForUpdate blocheaza randul camerei pana la finalul acestei metode,
        // ca doua rezervari simultane sa nu treaca amandoua de verificare
        Room room = roomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        boolean free = findAvailableRooms(room.getProperty().getId(), in, out, guests)
                .stream().anyMatch(r -> r.getId().equals(roomId));
        if (!free) {
            throw new IllegalStateException("Room no longer available");
        }
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setCheckIn(in);
        booking.setCheckOut(out);
        booking.setGuestName(guestName);
        booking.setGuests(guests);
        bookingRepository.save(booking);
    }
    public List<Booking> findBookings(String guestName) {
        return bookingRepository.findByGuestNameContainingIgnoreCaseOrderByCheckInDesc(guestName);
    }

    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}