package bankcards.dto;

import bankcards.entity.Card;
import bankcards.entity.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public CardDTO(Card card) {
        this.id = card.getId();
        this.maskedNumber = maskCardNumber(card.getCardNumber());
        this.expirationDate = card.getExpirationDate();
        this.status = card.getStatus();
        this.balance = card.getBalance();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}