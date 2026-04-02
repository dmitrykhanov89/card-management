package bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * <p>
 * Data Transfer Object (DTO) для запроса на регистрацию нового пользователя.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Содержит логин и пароль пользователя</li>
 *     <li>Используется при вызове API регистрации пользователя</li>
 *     <li>Пароль передаётся в зашифрованном виде на сервере (хэшируется перед сохранением)</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для регистрации нового пользователя")
public class RegisterRequest {

    @Schema(description = "Логин пользователя", example = "john_doe")
    private String username;

    @Schema(description = "Пароль пользователя", example = "strongPassword123")
    private String password;
}