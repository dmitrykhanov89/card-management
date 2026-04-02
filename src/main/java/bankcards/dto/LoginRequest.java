package bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Data Transfer Object (DTO) для запроса аутентификации пользователя (логина).
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Содержит имя пользователя (username) и пароль</li>
 *     <li>Используется при вызове API для аутентификации</li>
 *     <li>Пароль передаётся на сервер для проверки соответствия хэшу в базе данных</li>
 * </ul>
 */
@Data
@Schema(description = "DTO для запроса логина пользователя")
public class LoginRequest {

    @Schema(description = "Имя пользователя", example = "john_doe")
    private String username;

    @Schema(description = "Пароль пользователя", example = "P@ssw0rd")
    private String password;
}