package magis.mundi2025.demo.service;
import magis.mundi2025.demo.model.entity.Bookings;
import magis.mundi2025.demo.repository.BookingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final BookingsRepository bookingsRepository;

    @Autowired
    public BookingService(BookingsRepository bookingsRepository) {
        this.bookingsRepository = bookingsRepository;
    }

    public Bookings createBooking(Bookings booking) {

        return bookingsRepository.save(booking);
    }
}
