package bankcards.dto;

import bankcards.entity.Transfer;
import lombok.*;

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

    public static TransferDTO fromEntity(Transfer transfer) {
        return new TransferDTO(transfer.getId(), mask(transfer.getFromCard().getCardNumber()),
                mask(transfer.getToCard().getCardNumber()), transfer.getAmount(), transfer.getCreatedAt());
    }

    private static String mask(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}