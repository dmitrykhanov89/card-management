package bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для запроса логина пользователя")
public class LoginRequest {

    @Schema(description = "Имя пользователя", example = "john_doe")
    private String username;

    @Schema(description = "Пароль пользователя", example = "P@ssw0rd")
    private String password;
}