package magis.mundi2025.demo.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.converter.PropertyConverter;
import magis.mundi2025.demo.service.CurrencyService;
import magis.mundi2025.demo.service.PropertyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import magis.mundi2025.demo.model.entity.Property;

import java.time.LocalDate;
import java.util.List;
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

        LocalDate in = checkIn != null ? checkIn : LocalDate.now();

        // NOU: daca checkOut nu e dupa checkIn, aratam eroare in loc sa le interschimbam tacut
        String dateError = null;
        LocalDate out = checkOut;
        if (out != null && !out.isAfter(in)) {
            dateError = "Check-out date must be after check-in date.";
            out = null;
        }
        if (out == null) {
            out = in.plusDays(1);
        }

        final LocalDate finalIn = in;
        final LocalDate finalOut = out;

        // NOU: cautam hotelurile inainte de a filtra dupa disponibilitate,
        // ca sa stim daca lipsa rezultatelor vine din "nu exista" sau din "nu are camere libere"
        List<Property> matchingProperties = propertyService.search(q);

        var propertyDTOs = matchingProperties.stream()
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
        model.addAttribute("dateError", dateError);
        // NOU: adevarat doar daca s-a cautat ceva si nu exista niciun hotel cu numele/adresa aceea
        model.addAttribute("noMatch", !q.isBlank() && matchingProperties.isEmpty());

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

        LocalDate in = checkIn != null ? checkIn : LocalDate.now();

        String dateError = null;
        LocalDate out = checkOut;
        if (out != null && !out.isAfter(in)) {
            dateError = "Check-out date must be after check-in date.";
            out = null;
        }
        if (out == null) {
            out = in.plusDays(1);
        }

        var property = propertyService.getPropertyById(id);

        // NOU: camerele ocupate pe acele date (indiferent de capacitate)
        Set<Long> bookedRoomIds = propertyService.findBookedRoomIds(id, in, out);

        model.addAttribute("property", propertyConverter.convertToDTO(property));
        model.addAttribute("bookedRoomIds", bookedRoomIds);
        model.addAttribute("checkIn", in);
        model.addAttribute("checkOut", out);
        model.addAttribute("guests", guests);
        model.addAttribute("booked", booked);
        model.addAttribute("error", error);
        model.addAttribute("dateError", dateError);

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

        if (wanted != null && !wanted.equals(chosen)) {
            model.addAttribute("currencyNote",
                    "Could not use " + wanted + " (reason: " + currencyService.getLastMessage()
                            + "). Prices are shown in EUR.");
        }
    }
}