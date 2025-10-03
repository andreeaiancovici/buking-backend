package magis.mundi2025.demo.controller;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.model.entity.Booking;
import magis.mundi2025.demo.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingViewController {

    private final BookingService bookingService;

    // butonul "Book Now" apelează acest endpoint
    @PostMapping("/create")
    public String createBooking(@RequestParam("roomId") Long roomId,
                                Model model) {
        // apelăm service-ul corect
        Booking booking = bookingService.createBookingForRoom(roomId);

        // mesaj de confirmare
        model.addAttribute("message", "Booking created successfully! ID = " + booking.getId());

        // rămânem în pagina property (sau faci redirect dacă vrei)
        return "property";
    }

}
