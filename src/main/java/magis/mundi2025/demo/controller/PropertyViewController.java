package magis.mundi2025.demo.controller;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.converter.PropertyConverter;
import magis.mundi2025.demo.model.entity.Room;
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

    @GetMapping("/properties")
    public String viewAllProperties(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "2") int guests,
            Model model) {
        LocalDate in = checkIn != null ? checkIn : LocalDate.now();
        LocalDate out = (checkOut != null && checkOut.isAfter(in)) ? checkOut : in.plusDays(1);

        var propertyDTOs = propertyService.search(q).stream()
                .map(p -> {
                    var dto = propertyConverter.convertToDTO(p);
                    dto.setAvailableRooms(propertyService.countAvailable(p.getId(), in, out, guests));
                    return dto;
                })
                .filter(dto -> dto.getAvailableRooms() > 0)
                .collect(Collectors.toList());

        model.addAttribute("properties", propertyDTOs);
        model.addAttribute("q", q);
        model.addAttribute("checkIn", in);
        model.addAttribute("checkOut", out);
        model.addAttribute("guests", guests);
        return "properties";
    }

    @GetMapping("/properties/{id}")
    public String viewPropertyDetails(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "2") int guests,
            @RequestParam(defaultValue = "false") boolean booked,
            @RequestParam(defaultValue = "false") boolean error,
            Model model) {
        LocalDate in = checkIn != null ? checkIn : LocalDate.now();
        LocalDate out = (checkOut != null && checkOut.isAfter(in)) ? checkOut : in.plusDays(1);

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
        return "property-details";
    }

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
}