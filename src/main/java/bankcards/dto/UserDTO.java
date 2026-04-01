package bankcards.dto;

import bankcards.entity.Role;
import bankcards.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO пользователя")
public class UserDTO {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "john_doe")
    private String username;

    @Schema(description = "Роли пользователя", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<String> roles;

    public static UserDTO fromEntity(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return new UserDTO(user.getId(), user.getUsername(), roleNames);
    }
}