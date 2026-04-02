package bankcards.security;

import bankcards.entity.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Objects;

/**
 * <p>
 * Сервис безопасности для проверки доступа к банковским картам.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Проверка прав доступа к карте для текущего пользователя</li>
 *     <li>Администраторы имеют доступ ко всем картам</li>
 *     <li>Обычные пользователи могут работать только со своими картами</li>
 *     <li>Выбрасывание {@link AccessDeniedException} при попытке доступа к чужой карте</li>
 * </ul>
 *
 * <p>Используется в сервисах и контроллерах для защиты бизнес-логики и ресурсов.</p>
 */
@Service
@RequiredArgsConstructor
public class SecurityService {

    /**
     * Проверяет, имеет ли текущий пользователь доступ к указанной карте.
     *
     * @param card карта, к которой проверяется доступ
     * @throws AccessDeniedException если пользователь не имеет прав доступа к карте
     */
    public void validateCardAccess(Card card) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String username = Objects.requireNonNull(auth).getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin && !card.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("Пользователь не имеет прав доступа к карте");
        }
    }
}