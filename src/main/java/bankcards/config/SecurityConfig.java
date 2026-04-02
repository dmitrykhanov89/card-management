package bankcards.config;

import bankcards.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <p>
 * Конфигурация безопасности Spring Security для приложения управления банковскими картами.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Настройка JWT-аутентификации через {@link JwtAuthenticationFilter}</li>
 *     <li>Определение правил доступа к API по ролям (ADMIN, USER)</li>
 *     <li>Отключение CSRF для stateless API</li>
 *     <li>Использование stateless сессий (JWT)</li>
 *     <li>Настройка открытых путей для Swagger UI и публичных эндпоинтов</li>
 *     <li>Бин {@link PasswordEncoder} для хэширования паролей</li>
 *     <li>Бин {@link AuthenticationManager} для управления аутентификацией</li>
 * </ul>
 */
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Настраивает цепочку фильтров безопасности Spring Security.
     *
     * @param http объект {@link HttpSecurity} для конфигурации
     * @return объект {@link SecurityFilterChain}, применяемый в приложении
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs.yaml").permitAll()
                        // Регистрация и логин обычных пользователей
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        // Регистрация админа — только ADMIN
                        .requestMatchers("/api/auth/admin-register").hasRole("ADMIN")
                        // Пользователи — только ADMIN
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        // Карты и переводы — USER и ADMIN
                        .requestMatchers("/api/cards/**", "/api/transfers/**").hasAnyRole("USER", "ADMIN")
                        // Всё остальное — аутентификация обязательна
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Создаёт бин {@link AuthenticationManager} для управления аутентификацией.
     *
     * @param authConfig объект {@link AuthenticationConfiguration}
     * @return бин {@link AuthenticationManager}
     * @throws Exception если не удалось создать менеджер аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Создаёт бин {@link PasswordEncoder} для хэширования паролей пользователей.
     *
     * @return объект {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}