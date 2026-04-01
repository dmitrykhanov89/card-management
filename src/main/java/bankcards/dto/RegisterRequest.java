package bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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