package magis.mundi2025.demo.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingDTO {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    private String status;

    // Trimitem doar ID-urile pentru referință, ca Frontend-ul să știe
    // cine a făcut rezervarea și pentru ce cameră.
    private Long userId;
    private Long roomId;
}