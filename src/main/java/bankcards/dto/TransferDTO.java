package bankcards.dto;

import bankcards.entity.Transfer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * Data Transfer Object (DTO) для перевода между банковскими картами.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Содержит ID перевода, замаскированные номера карт отправителя и получателя</li>
 *     <li>Хранит сумму перевода и дату/время создания</li>
 *     <li>Предоставляет метод {@link #fromEntity(Transfer)} для преобразования сущности {@link Transfer} в DTO</li>
 *     <li>Маскирует номера карт, чтобы скрыть чувствительные данные</li>
 * </ul>
 */
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

    /**
     * Преобразует сущность {@link Transfer} в объект {@link TransferDTO}.
     *
     * @param transfer сущность {@link Transfer}
     * @return объект {@link TransferDTO} с замаскированными номерами карт, суммой и датой/временем создания
     */
    public static TransferDTO fromEntity(Transfer transfer) {
        return new TransferDTO(transfer.getId(), mask(transfer.getFromCard().getCardNumber()),
                mask(transfer.getToCard().getCardNumber()), transfer.getAmount(), transfer.getCreatedAt());
    }

    /**
     * Маскирует номер карты, оставляя только последние 4 цифры.
     *
     * @param number номер карты
     * @return замаскированный номер карты вида "**** **** **** 1234"
     */
    private static String mask(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}