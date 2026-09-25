package magis.mundi2025.demo.controller;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.converter.PropertyConverter;
import magis.mundi2025.demo.service.PropertyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PropertyViewController {
    private final PropertyService propertyService;
    private final PropertyConverter propertyConverter;

    @GetMapping("/properties")
    public String viewAllProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            Model model) {

        // Pastram valorile in model ca sa nu se goleasca formularul
        model.addAttribute("city", city);
        model.addAttribute("minRating", minRating);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minCapacity", minCapacity);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);

        // Validari cu mesaje clare
        if (minRating != null && (minRating < 1 || minRating > 5)) {
            model.addAttribute("errorMessage", "Rating-ul trebuie să fie între 1 și 5 stele!");
            model.addAttribute("properties", Collections.emptyList());
            return "properties";
        }

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            model.addAttribute("errorMessage", "Prețul minim nu poate fi negativ!");
            model.addAttribute("properties", Collections.emptyList());
            return "properties";
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            model.addAttribute("errorMessage", "Prețul maxim nu poate fi negativ!");
            model.addAttribute("properties", Collections.emptyList());
            return "properties";
        }

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            model.addAttribute("errorMessage", "Prețul minim nu poate fi mai mare decât prețul maxim!");
            model.addAttribute("properties", Collections.emptyList());
            return "properties";
        }

        if (minCapacity != null && minCapacity <= 0) {
            model.addAttribute("errorMessage", "Numărul minim de oaspeți trebuie să fie cel puțin 1!");
            model.addAttribute("properties", Collections.emptyList());
            return "properties";
        }

        if (checkIn != null && checkOut != null) {
            if (checkIn.isBefore(LocalDate.now())) {
                model.addAttribute("errorMessage", "Data de check-in nu poate fi în trecut!");
                model.addAttribute("properties", Collections.emptyList());
                return "properties";
            }
            if (!checkOut.isAfter(checkIn)) {
                model.addAttribute("errorMessage", "Data de check-out trebuie să fie după data de check-in!");
                model.addAttribute("properties", Collections.emptyList());
                return "properties";
            }
        } else if ((checkIn != null && checkOut == null) || (checkIn == null && checkOut != null)) {
            model.addAttribute("errorMessage", "Te rugăm să selectezi ambele date (check-in și check-out)!");
            model.addAttribute("properties", Collections.emptyList());
            return "properties";
        }

        try {
            var properties = propertyService.searchProperties(
                    city, minRating, minPrice, maxPrice, minCapacity, checkIn, checkOut);
            var propertyDTOs = properties.stream()
                    .map(propertyConverter::convertToDTO)
                    .collect(Collectors.toList());
            model.addAttribute("properties", propertyDTOs);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Eroare la căutare: " + e.getMessage());
            model.addAttribute("properties", Collections.emptyList());
        }

        return "properties";
    }

    @GetMapping("/properties/{id}")
    public String viewPropertyDetails(@PathVariable Long id, Model model) {
        var property = propertyService.getPropertyById(id);
        var propertyDTO = propertyConverter.convertToDTO(property);
        model.addAttribute("property", propertyDTO);
        return "property-details";
    }
}