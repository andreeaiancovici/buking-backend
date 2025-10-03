package magis.mundi2025.demo.controller;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.model.entity.Booking;
import magis.mundi2025.demo.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/api/book")
    public Map<String, Object> createBooking(@RequestBody Map<String, Long> request) {
        Long roomId = request.get("roomId");

        Booking booking = bookingService.createBookingForRoom(roomId);

        // răspuns JSON
        Map<String, Object> response = new HashMap<>();
        response.put("id", booking.getId());
        response.put("roomId", booking.getRoom().getId());
        response.put("message", "Booking created successfully!");
        return response;
    }
}
