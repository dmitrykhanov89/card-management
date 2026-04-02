package bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * Data Transfer Object (DTO) для запроса на создание новой банковской карты.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Содержит номер карты и дату окончания действия</li>
 *     <li>Содержит ID владельца карты</li>
 *     <li>Содержит начальный баланс карты</li>
 *     <li>Используется в API при создании карты администратором</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для создания новой банковской карты")
public class CardCreateDTO {

    @NotBlank(message = "Номер карты обязателен")
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать 16 цифр")
    @Schema(description = "Номер карты", example = "1234123412341234")
    private String cardNumber;

    @NotNull(message = "Дата окончания действия обязательна")
    @Future(message = "Дата окончания действия должна быть в будущем")
    @Schema(description = "Дата окончания действия карты", example = "2026-12-31")
    private LocalDate expirationDate;

    @NotNull(message = "ID владельца обязателен")
    @Schema(description = "ID владельца карты", example = "1")
    private Long ownerId;

    @NotNull(message = "Баланс обязателен")
    @DecimalMin(value = "0.0", message = "Баланс не может быть отрицательным")
    @Schema(description = "Начальный баланс карты", example = "1000.50")
    private BigDecimal balance;
}