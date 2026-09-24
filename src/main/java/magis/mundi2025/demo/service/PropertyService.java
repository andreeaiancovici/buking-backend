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
import java.util.List;

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

    @Transactional
    public void book(Long roomId, LocalDate in, LocalDate out, String guestName, int guests) {
        Room room = roomRepository.findById(roomId)
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
}