package bankcards.dto;

import bankcards.entity.Transfer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO перевода между картами")
public class TransferDTO {

    @Schema(description = "ID перевода", example = "1")
    private Long id;

    @Schema(description = "Номер карты отправителя (замаскированный)", example = "**** **** **** 1234")
    private String fromCardMasked;

    @Schema(description = "Номер карты получателя (замаскированный)", example = "**** **** **** 5678")
    private String toCardMasked;

    @Schema(description = "Сумма перевода", example = "1000.50")
    private BigDecimal amount;

    @Schema(description = "Дата и время создания перевода", example = "2026-04-01T12:30:45")
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