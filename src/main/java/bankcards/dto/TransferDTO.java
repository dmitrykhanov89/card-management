package bankcards.dto;

import bankcards.entity.Transfer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {

    private Long id;
    private String fromCardMasked;
    private String toCardMasked;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public TransferDTO(Transfer transfer) {
        this.id = transfer.getId();
        this.fromCardMasked = maskCardNumber(transfer.getFromCard().getCardNumber());
        this.toCardMasked = maskCardNumber(transfer.getToCard().getCardNumber());
        this.amount = transfer.getAmount();
        this.createdAt = transfer.getCreatedAt();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}