package magis.mundi2025.demo.model.dto;

import lombok.Data;
import magis.mundi2025.demo.model.entity.BookingStatus;

import java.time.LocalDate;

@Data
public class BookingDTO {

    private Long Id;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private BookingStatus status;

    private Long userId;

    private Long roomId;
}