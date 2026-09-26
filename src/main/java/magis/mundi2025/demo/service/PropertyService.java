package magis.mundi2025.demo.service;

import lombok.RequiredArgsConstructor;
import magis.mundi2025.demo.model.entity.Property;
import magis.mundi2025.demo.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

    public List<Property> searchProperties(String city, Integer minRating, BigDecimal minPrice,
                                           BigDecimal maxPrice, Integer minCapacity,
                                           LocalDate checkIn, LocalDate checkOut) {
        if (checkIn != null && checkOut != null && !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        String normalizedCity = (city == null || city.isBlank()) ? null : city.trim();

        return propertyRepository.search(normalizedCity, minRating, minPrice, maxPrice, minCapacity);
    }
}
