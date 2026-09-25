package magis.mundi2025.demo.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.converter.PropertyConverter;
import magis.mundi2025.demo.model.entity.Property;
import magis.mundi2025.demo.service.CurrencyService;
import magis.mundi2025.demo.service.PropertyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookingViewController {
    private final PropertyService propertyService;

    @GetMapping("/bookings")
    public String myBookings(@RequestParam(defaultValue = "") String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("bookings",
                name.isBlank() ? java.util.List.of() : propertyService.findBookings(name));
        return "bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancel(@PathVariable Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        propertyService.cancelBooking(id);
        redirectAttributes.addAttribute("name", name);
        return "redirect:/bookings";
    }
}