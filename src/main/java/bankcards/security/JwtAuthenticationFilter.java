package bankcards.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import bankcards.service.UserDetailsServiceImpl;
import java.io.IOException;

/**
 * <p>
 * Фильтр для аутентификации JWT, выполняющий проверку токена на каждом запросе.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Извлечение JWT из заголовка Authorization</li>
 *     <li>Проверка валидности токена через {@link JwtUtils}</li>
 *     <li>Установка аутентификации пользователя в {@link SecurityContextHolder}</li>
 *     <li>Пропуск запросов на регистрацию и логин</li>
 * </ul>
 *
 * <p>Используется в цепочке фильтров Spring Security для обеспечения безопасного доступа к API.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Выполняет фильтрацию запроса, проверяет JWT и устанавливает аутентификацию пользователя.
     *
     * @param request  объект запроса {@link HttpServletRequest}
     * @param response объект ответа {@link HttpServletResponse}
     * @param filterChain цепочка фильтров {@link FilterChain}
     * @throws ServletException если происходит ошибка сервлета
     * @throws IOException если происходит ошибка ввода/вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtils.getUsernameFromToken(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.trace("JWT authentication set for user: {}", username);
            } else {
                log.warn("Invalid JWT token for user: {}", username);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Определяет, следует ли пропустить фильтрацию для определённых путей.
     *
     * @param request объект запроса {@link HttpServletRequest}
     * @return true, если фильтр не должен применяться к запросу (например, регистрация или логин)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/auth/register") || path.equals("/api/auth/login");
    }
}