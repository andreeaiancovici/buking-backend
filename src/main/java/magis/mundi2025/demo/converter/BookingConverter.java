package magis.mundi2025.demo.converter;

import magis.mundi2025.demo.model.dto.BookingDTO;
import magis.mundi2025.demo.model.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingConverter {

    public BookingDTO convertToDTO(Booking booking) {

        BookingDTO dto = new BookingDTO();

        dto.setId(booking.getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setNumberOfGuests(booking.getNumberOfGuests());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());

        dto.setUserId(booking.getUser().getId());
        dto.setRoomId(booking.getRoom().getId());

        return dto;
    }
}