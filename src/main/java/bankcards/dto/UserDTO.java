package bankcards.dto;

import bankcards.entity.Role;
import bankcards.entity.User;
import lombok.*;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private Set<String> roles;

    public static UserDTO fromEntity(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return new UserDTO(user.getId(), user.getUsername(), roleNames);
    }
}