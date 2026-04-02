package bankcards.service;

import bankcards.entity.User;
import bankcards.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Реализация {@link UserDetailsService} для Spring Security.
 * Преобразует объект {@link User} в Spring Security {@link UserDetails}.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Загрузка пользователя по имени (username) для аутентификации</li>
 *     <li>Преобразование ролей пользователя в {@link SimpleGrantedAuthority}</li>
 *     <li>Выбрасывание {@link UsernameNotFoundException}, если пользователь не найден</li>
 * </ul>
 *
 * <p>Используется Spring Security при аутентификации через JWT.</p>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загружает пользователя по имени для аутентификации в Spring Security.
     *
     * @param username имя пользователя
     * @return объект {@link UserDetails}, содержащий имя, пароль и роли
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден
     */
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );
    }
}