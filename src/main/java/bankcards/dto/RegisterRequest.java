package bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов")
    @Schema(description = "Логин пользователя", example = "john_doe")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    @Schema(description = "Пароль пользователя", example = "strongPassword123")
    private String password;
}