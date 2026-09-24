package magis.mundi2025.demo.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.converter.PropertyConverter;
import magis.mundi2025.demo.model.entity.Room;
import magis.mundi2025.demo.service.CurrencyService;
import magis.mundi2025.demo.service.PropertyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PropertyViewController {

    private final PropertyService propertyService;
    private final PropertyConverter propertyConverter;
    private final CurrencyService currencyService;

    // ---------- Lista de hoteluri ----------
    @GetMapping("/properties")
    public String viewAllProperties(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "2") int guests,
            @RequestParam(required = false) String currency,
            HttpSession session,
            Model model) {

        LocalDate in = checkIn;
        if (in == null) {
            in = LocalDate.now();
        }

        LocalDate out = checkOut;
        if (out == null || !out.isAfter(in)) {
            out = in.plusDays(1);
        }

        final LocalDate finalIn = in;
        final LocalDate finalOut = out;

        var propertyDTOs = propertyService.search(q).stream()
                .map(p -> {
                    var dto = propertyConverter.convertToDTO(p);
                    dto.setAvailableRooms(
                            propertyService.countAvailable(p.getId(), finalIn, finalOut, guests));
                    return dto;
                })
                .filter(dto -> dto.getAvailableRooms() > 0)
                .collect(Collectors.toList());

        model.addAttribute("properties", propertyDTOs);
        model.addAttribute("q", q);
        model.addAttribute("checkIn", in);
        model.addAttribute("checkOut", out);
        model.addAttribute("guests", guests);

        addCurrency(currency, session, model);
        return "properties";
    }

    // ---------- Pagina unui hotel ----------
    @GetMapping("/properties/{id}")
    public String viewPropertyDetails(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "2") int guests,
            @RequestParam(defaultValue = "false") boolean booked,
            @RequestParam(defaultValue = "false") boolean error,
            @RequestParam(required = false) String currency,
            HttpSession session,
            Model model) {

        LocalDate in = checkIn;
        if (in == null) {
            in = LocalDate.now();
        }

        LocalDate out = checkOut;
        if (out == null || !out.isAfter(in)) {
            out = in.plusDays(1);
        }

        var property = propertyService.getPropertyById(id);

        Set<Long> availableRoomIds = propertyService.findAvailableRooms(id, in, out, guests)
                .stream().map(Room::getId).collect(Collectors.toSet());

        model.addAttribute("property", propertyConverter.convertToDTO(property));
        model.addAttribute("availableRoomIds", availableRoomIds);
        model.addAttribute("checkIn", in);
        model.addAttribute("checkOut", out);
        model.addAttribute("guests", guests);
        model.addAttribute("booked", booked);
        model.addAttribute("error", error);

        addCurrency(currency, session, model);
        return "property-details";
    }

    // ---------- Crearea unei rezervari ----------
    @PostMapping("/bookings")
    public String book(
            @RequestParam Long roomId,
            @RequestParam Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam int guests,
            @RequestParam String guestName) {

        String base = "redirect:/properties/" + propertyId
                + "?checkIn=" + checkIn + "&checkOut=" + checkOut + "&guests=" + guests;
        try {
            propertyService.book(roomId, checkIn, checkOut, guestName, guests);
            return base + "&booked=true";
        } catch (IllegalStateException e) {
            return base + "&error=true";
        }
    }

    // ---------- Moneda si cursul pentru pagina ----------
    private void addCurrency(String requested, HttpSession session, Model model) {

        if (requested != null) {
            session.setAttribute("currency", requested);
        }

        String wanted = (String) session.getAttribute("currency");
        String chosen = currencyService.resolve(wanted);

        model.addAttribute("currency", chosen);
        model.addAttribute("rate", currencyService.getRate(chosen));
        model.addAttribute("currencies", CurrencyService.SUPPORTED);

        // Daca nu am putut folosi moneda dorita, pagina spune si de ce
        if (wanted != null && !wanted.equals(chosen)) {
            model.addAttribute("currencyNote",
                    "Could not use " + wanted + " (reason: " + currencyService.getLastMessage()
                            + "). Prices are shown in EUR.");
        }
    }
}
