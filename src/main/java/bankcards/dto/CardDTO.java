package bankcards.dto;

import bankcards.entity.Card;
import bankcards.entity.CardStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {

    private Long id;
    private String maskedNumber;
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;

    public static CardDTO fromEntity(Card card) {
        return new CardDTO(card.getId(), mask(card.getCardNumber()), card.getExpirationDate(), card.getStatus(), card.getBalance());
    }

    private static String mask(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}