package bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * Data Transfer Object (DTO) для создания нового перевода между картами.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Содержит ID карты отправителя и получателя</li>
 *     <li>Содержит сумму перевода</li>
 *     <li>Используется при вызове API для создания перевода</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания перевода между картами")
public class TransferCreateDTO {

    @Schema(description = "ID карты отправителя", example = "1")
    private Long fromCardId;

    @Schema(description = "ID карты получателя", example = "2")
    private Long toCardId;

    @Schema(description = "Сумма перевода", example = "1000.50")
    private BigDecimal amount;
}