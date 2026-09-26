package magis.mundi2025.demo.controller;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.converter.PropertyConverter;
import magis.mundi2025.demo.model.dto.PropertyDTO;
import magis.mundi2025.demo.service.PropertyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;
    private final PropertyConverter propertyConverter;

    @GetMapping
    public ResponseEntity<List<PropertyDTO>> getAllProperties() {
        var properties = propertyService.getAllProperties();
        var propertyDTOs = properties.stream()
                .map(propertyConverter::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(propertyDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
        var property = propertyService.getPropertyById(id);
        var propertyDTO = propertyConverter.convertToDTO(property);
        return ResponseEntity.ok(propertyDTO);

    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        if (minRating != null && (minRating < 1 || minRating > 5)) {
            return ResponseEntity.badRequest().body("Rating-ul minim trebuie să fie între 1 și 5.");
        }

        // 2. Validare Prețuri negative
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.badRequest().body("Prețul minim nu poate fi negativ.");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.badRequest().body("Prețul maxim nu poate fi negativ.");
        }

        // 3. Validare Interval Preț (minPrice <= maxPrice)
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            return ResponseEntity.badRequest().body("Prețul minim nu poate fi mai mare decât prețul maxim.");
        }

        // 4. Validare Capacitate
        if (minCapacity != null && minCapacity <= 0) {
            return ResponseEntity.badRequest().body("Capacitatea minimă trebuie să fie de cel puțin o persoană.");
        }

        // 5. Validare Calendar (Date sejur)
        if (checkIn != null && checkOut == null) {
            return ResponseEntity.badRequest().body("Trebuie să selectezi și o dată de check-out.");
        }
        if (checkIn == null && checkOut != null) {
            return ResponseEntity.badRequest().body("Trebuie să selectezi și o dată de check-in.");
        }
        if (checkIn != null && checkOut != null) {
            LocalDate today = LocalDate.now();
            if (checkIn.isBefore(today)) {
                return ResponseEntity.badRequest().body("Data de check-in nu poate fi în trecut.");
            }
            if (!checkOut.isAfter(checkIn)) {
                return ResponseEntity.badRequest().body("Data de check-out trebuie să fie după data de check-in.");
            }
        }

        try {
            var properties = propertyService.searchProperties(
                    city, minRating, minPrice, maxPrice, minCapacity, checkIn, checkOut);
            var propertyDTOs = properties.stream()
                    .map(propertyConverter::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(propertyDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}