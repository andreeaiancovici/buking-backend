package magis.mundi2025.demo.model.dto;

import lombok.Data;
import magis.mundi2025.demo.model.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingDTO {

    private Long id;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer numberOfGuests;

    private BigDecimal totalPrice;

    private BookingStatus status;

    private Long userId;

    private Long roomId;
}