package magis.mundi2025.demo.converter;

import magis.mundi2025.demo.model.dto.BookingDTO;
import magis.mundi2025.demo.model.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingConverter {
    public BookingDTO toDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}