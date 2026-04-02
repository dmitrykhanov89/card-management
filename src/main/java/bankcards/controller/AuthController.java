package bankcards.controller;

import bankcards.dto.LoginRequest;
import bankcards.dto.LoginResponse;
import bankcards.dto.RegisterRequest;
import bankcards.entity.Role;
import bankcards.entity.User;
import bankcards.exception.BusinessException;
import bankcards.security.JwtUtils;
import bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

/**
 * <p>
 * Контроллер аутентификации и регистрации пользователей.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Регистрация нового пользователя с ролью USER</li>
 *     <li>Аутентификация пользователя и получение JWT-токена</li>
 *     <li>Регистрация администратора с ролью ADMIN (только для существующих админов)</li>
 * </ul>
 *
 * <p>Все операции возвращают {@link ResponseEntity} с результатом действия.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрация нового пользователя с ролью USER.
     *
     * @param request объект {@link RegisterRequest} с данными нового пользователя
     * @return {@link ResponseEntity} с сообщением о результате регистрации
     * @throws BusinessException если имя пользователя уже занято
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Регистрация нового пользователя с ролью USER")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {throw new BusinessException("Имя пользователя уже занято");}
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = userService.getRoleByName("ROLE_USER");
        user.setRoles(Set.of(role));
        userService.createUser(user);
        return ResponseEntity.ok("User registered");
    }

    /**
     * Аутентификация пользователя и получение JWT токена.
     *
     * @param request объект {@link LoginRequest} с именем пользователя и паролем
     * @return {@link ResponseEntity} с {@link LoginResponse}, содержащим JWT токен
     */
    @PostMapping("/login")
    @Operation(summary = "Вход пользователя", description = "Аутентификация пользователя и получение JWT токена")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtUtils.generateToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    /**
     * Регистрация нового администратора с ролью ADMIN.
     * <p>Доступно только для пользователей с ролью ADMIN.</p>
     *
     * @param request объект {@link RegisterRequest} с данными нового администратора
     * @return {@link ResponseEntity} с сообщением о результате регистрации
     * @throws RuntimeException если имя пользователя уже занято
     */
    @PostMapping("/admin-register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Регистрация администратора", description = "Регистрация нового пользователя с ролью ADMIN (только для администраторов)")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {throw new BusinessException("Имя пользователя уже занято");}
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = userService.getRoleByName("ROLE_ADMIN");
        user.setRoles(Set.of(role));
        userService.createUser(user);
        return ResponseEntity.ok("Admin registered");
    }
}