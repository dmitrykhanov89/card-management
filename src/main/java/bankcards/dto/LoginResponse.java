package bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 * Data Transfer Object (DTO) для ответа при успешной аутентификации пользователя.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Содержит JWT токен, который используется для последующих запросов к защищённым API</li>
 *     <li>Используется в ответе API после успешного логина</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@Schema(description = "DTO для ответа при успешном логине")
public class LoginResponse {

    @Schema(description = "JWT токен для аутентификации", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}