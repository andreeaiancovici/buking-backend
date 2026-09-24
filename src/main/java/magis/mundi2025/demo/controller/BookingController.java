package magis.mundi2025.demo.controller;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.model.entity.Booking;
import magis.mundi2025.demo.model.entity.Room;
import magis.mundi2025.demo.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/bookings/new")
    public String showBookingForm(
            @RequestParam Long roomId,
            Model model) {

        Room room = bookingService.getRoomById(roomId);

        model.addAttribute("room", room);

        return "booking-form";
    }

    @PostMapping("/bookings")
    public String createBooking(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam Long roomId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate,
            @RequestParam Integer numberOfGuests,
            Model model) {

        try {

            Booking booking = bookingService.createBooking(
                    name,
                    email,
                    roomId,
                    checkInDate,
                    checkOutDate,
                    numberOfGuests
            );

            model.addAttribute("booking", booking);

            return "booking-success";

        } catch (RuntimeException e) {

            Room room = bookingService.getRoomById(roomId);

            model.addAttribute("room", room);
            model.addAttribute("error", e.getMessage());

            return "booking-form";
        }
    }
}