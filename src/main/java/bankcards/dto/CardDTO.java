package bankcards.dto;

import bankcards.entity.Card;
import bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * Data Transfer Object (DTO) для представления информации о банковской карте.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Содержит маскированный номер карты</li>
 *     <li>Содержит статус карты и дату окончания действия</li>
 *     <li>Содержит текущий баланс и информацию о запросе на блокировку</li>
 *     <li>Предоставляет метод {@link #fromEntity(Card)} для преобразования сущности {@link Card} в DTO</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для представления банковской карты")
public class CardDTO {

    @Schema(description = "ID карты", example = "1")
    private Long id;

    @Schema(description = "Маскированный номер карты", example = "**** **** **** 1234")
    private String maskedNumber;

    @Schema(description = "Дата окончания действия карты", example = "2026-12-31")
    private LocalDate expirationDate;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Баланс карты", example = "1000.50")
    private BigDecimal balance;

    @Schema(description = "Запрошена блокировка карты", example = "false")
    private boolean blockRequested;

    public static CardDTO fromEntity(Card card) {
        return new CardDTO(card.getId(), mask(card.getCardNumber()), card.getExpirationDate(), card.getStatus(), card.getBalance(),
                card.isBlockRequested());
    }

    private static String mask(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}