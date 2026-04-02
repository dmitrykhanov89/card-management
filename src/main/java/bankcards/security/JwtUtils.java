package bankcards.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * <p>
 * Утилитарный класс для работы с JWT (JSON Web Token).
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Генерация JWT-токенов для пользователей</li>
 *     <li>Извлечение имени пользователя из JWT</li>
 *     <li>Проверка валидности JWT</li>
 * </ul>
 *
 * <p>Используется в механизме аутентификации и авторизации через JWT.</p>
 */
@Slf4j
@Component
public class JwtUtils {

    /** Секретный ключ для подписи токена */
    private final String jwtSecret = "my-secret-key-my-secret-key-my-secret-key";

    /** Время жизни токена в миллисекундах (по умолчанию 24 часа) */
    private final long jwtExpirationMs = 24 * 60 * 60 * 1000;

    /**
     * Возвращает объект ключа для подписи JWT.
     *
     * @return объект {@link Key} для HMAC SHA подписи
     */
    private Key getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Генерирует JWT-токен для указанного пользователя.
     *
     * @param username имя пользователя
     * @return сгенерированный JWT-токен
     */
    public String generateToken(String username) {
        log.debug("Generating JWT token for user: {}", username);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getKey())
                .compact();
    }

    /**
     * Извлекает имя пользователя из JWT-токена.
     *
     * @param token JWT-токен
     * @return имя пользователя (subject)
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Проверяет валидность JWT-токена.
     *
     * @param token JWT-токен
     * @return true, если токен корректен и не истёк, иначе false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}