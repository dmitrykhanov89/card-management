package bankcards.controller;

import bankcards.dto.UserDTO;
import bankcards.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * Контроллер для управления пользователями.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Просмотр всех пользователей (только ADMIN)</li>
 *     <li>Просмотр информации о конкретном пользователе по username</li>
 *     <li>Администратор может просматривать любого пользователя, обычный пользователь — только себя</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    /**
     * Получает список всех пользователей (только ADMIN).
     *
     * @return {@link ResponseEntity} с {@link List} {@link UserDTO} всех пользователей
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей (только для админа)")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllDTO());
    }

    /**
     * Получает информацию о пользователе по username.
     *
     * <p>Доступ:</p>
     * <ul>
     *     <li>ADMIN может получить любого пользователя</li>
     *     <li>Обычный пользователь может получить только себя</li>
     * </ul>
     *
     * @param username имя пользователя (username)
     * @return {@link ResponseEntity} с {@link UserDTO} пользователя
     */
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить пользователя по имени", description = "Возвращает информацию о пользователе по его username")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsernameDTO(username));
    }
}