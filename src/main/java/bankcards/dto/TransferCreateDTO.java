package bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "ID карты отправителя обязателен")
    @Schema(description = "ID карты отправителя", example = "1")
    private Long fromCardId;

    @NotNull(message = "ID карты получателя обязателен")
    @Schema(description = "ID карты получателя", example = "2")
    private Long toCardId;

    @NotNull(message = "Сумма перевода обязательна")
    @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше 0")
    @Schema(description = "Сумма перевода", example = "1000.50")
    private BigDecimal amount;
}