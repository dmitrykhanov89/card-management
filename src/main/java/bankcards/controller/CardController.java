package bankcards.controller;

import bankcards.dto.CardDTO;
import bankcards.dto.CardCreateDTO;
import bankcards.entity.CardStatus;
import bankcards.entity.User;
import bankcards.exception.ResourceNotFoundException;
import bankcards.service.CardService;
import bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

/**
 * <p>
 * Контроллер для управления банковскими картами.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Создание, обновление и удаление карт (только ADMIN)</li>
 *     <li>Просмотр карт конкретного пользователя (ADMIN)</li>
 *     <li>Просмотр своих карт и баланса (USER и ADMIN)</li>
 *     <li>Запрос блокировки карты пользователем</li>
 *     <li>Постраничная выдача списков карт</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    /**
     * Создаёт новую карту для пользователя (ADMIN).
     *
     * @param dto объект {@link CardCreateDTO} с данными карты
     * @return {@link ResponseEntity} с {@link CardDTO} созданной карты
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать карту", description = "Позволяет администратору создать новую карту для пользователя")
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardCreateDTO dto) {
        return ResponseEntity.ok(cardService.createCard(dto.getOwnerId(), dto.getCardNumber(), dto.getExpirationDate(), dto.getBalance()));
    }

    /**
     * Получает информацию о карте по ID (ADMIN).
     *
     * @param id идентификатор карты
     * @return {@link ResponseEntity} с {@link CardDTO} карты
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить карту по ID", description = "Возвращает информацию о карте по её идентификатору")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getById(id));
    }

    /**
     * Получает список карт конкретного пользователя с пагинацией (ADMIN).
     *
     * @param userId идентификатор пользователя
     * @param page номер страницы
     * @param size размер страницы
     * @return {@link ResponseEntity} с {@link Page} {@link CardDTO} карт пользователя
     * @throws ResourceNotFoundException если пользователь не найден
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить карты пользователя", description = "Возвращает список карт указанного пользователя с пагинацией и фильтрацией")
    public ResponseEntity<Page<CardDTO>> getUserCards(
            @PathVariable Long userId,
            @RequestParam(required = false) CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userService.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(cardService.getUserCards(user, status, PageRequest.of(page, size)));
    }

    /**
     * Обновляет статус карты (ADMIN).
     *
     * @param id идентификатор карты
     * @param status новый статус карты {@link CardStatus}
     * @return {@link ResponseEntity} с обновлённой {@link CardDTO} карты
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить статус карты", description = "Позволяет администратору изменить статус карты (ACTIVE, BLOCKED, EXPIRED)")
    public ResponseEntity<CardDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam CardStatus status) {
        return ResponseEntity.ok(cardService.updateStatus(id, status));
    }

    /**
     * Получает баланс указанной карты (USER и ADMIN).
     *
     * @param id идентификатор карты
     * @return {@link ResponseEntity} с балансом карты
     */
    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Посмотреть баланс карты", description = "Возвращает текущий баланс указанной карты")
    public ResponseEntity<?> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getBalance(id));
    }

    /**
     * Получает список карт текущего пользователя с пагинацией (USER и ADMIN).
     *
     * @param page номер страницы
     * @param size размер страницы
     * @return {@link ResponseEntity} с {@link Page} {@link CardDTO} карт текущего пользователя
     * @throws ResourceNotFoundException если пользователь не найден
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Получить свои карты", description = "Возвращает список карт текущего пользователя с пагинацией и фильтрацией")
    public ResponseEntity<Page<CardDTO>> getMyCards(
            @RequestParam(required = false) CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(cardService.getUserCards(user, status, PageRequest.of(page, size)));
    }

    /**
     * Удаляет карту по ID (ADMIN).
     *
     * @param id идентификатор карты
     * @return {@link ResponseEntity} с сообщением о результате удаления
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить карту", description = "Позволяет администратору удалить карту по её идентификатору")
    public ResponseEntity<?> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok("Card deleted");
    }

    /**
     * Запрашивает блокировку карты текущим пользователем (USER).
     *
     * @param id идентификатор карты
     * @return {@link ResponseEntity} с {@link CardDTO} карты после запроса блокировки
     */
    @PostMapping("/{id}/request-block")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Запросить блокировку карты", description = "Пользователь отправляет запрос на блокировку своей карты")
    public ResponseEntity<CardDTO> requestBlock(@PathVariable Long id) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return ResponseEntity.ok(cardService.requestBlock(id, username));
    }
}