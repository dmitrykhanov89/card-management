package bankcards.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateDTO {

    private String cardNumber;
    private LocalDate expirationDate;
    private Long ownerId;
    private BigDecimal balance;
}