package magis.mundi2025.demo.controller;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BookingViewController {

    private final BookingService bookingService;

    @GetMapping("/booking/{id}")
    public String viewBookingDetails(@PathVariable Long id, Model model) {
        // Asigură-te că ai adăugat metoda getBookingById în BookingService!
        var bookingDTO = bookingService.getBookingById(id);
        model.addAttribute("booking", bookingDTO);
        return "booking-details";
    }
}