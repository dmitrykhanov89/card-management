package bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Номер карты", example = "1234123412341234")
    private String cardNumber;

    @Schema(description = "Дата окончания действия карты", example = "2026-12-31")
    private LocalDate expirationDate;

    @Schema(description = "ID владельца карты", example = "1")
    private Long ownerId;

    @Schema(description = "Начальный баланс карты", example = "1000.50")
    private BigDecimal balance;
}