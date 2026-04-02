package bankcards.controller;

import bankcards.dto.CardDTO;
import bankcards.dto.CardCreateDTO;
import bankcards.entity.CardStatus;
import bankcards.entity.User;
import bankcards.exception.ResourceNotFoundException;
import bankcards.service.CardService;
import bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать карту", description = "Позволяет администратору создать новую карту для пользователя")
    public ResponseEntity<CardDTO> createCard(@RequestBody CardCreateDTO dto) {
        return ResponseEntity.ok(cardService.createCard(dto.getOwnerId(), dto.getCardNumber(), dto.getExpirationDate(), dto.getBalance()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить карту по ID", description = "Возвращает информацию о карте по её идентификатору")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить карты пользователя", description = "Возвращает список карт указанного пользователя с пагинацией")
    public ResponseEntity<Page<CardDTO>> getUserCards(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userService.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(cardService.getUserCards(user, PageRequest.of(page, size)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить статус карты", description = "Позволяет администратору изменить статус карты (ACTIVE, BLOCKED, EXPIRED)")
    public ResponseEntity<CardDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam CardStatus status) {
        return ResponseEntity.ok(cardService.updateStatus(id, status));
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Посмотреть баланс карты", description = "Возвращает текущий баланс указанной карты")
    public ResponseEntity<?> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getBalance(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Получить свои карты", description = "Возвращает список карт текущего пользователя с пагинацией")
    public ResponseEntity<Page<CardDTO>> getMyCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(cardService.getUserCards(user, PageRequest.of(page, size)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить карту", description = "Позволяет администратору удалить карту по её идентификатору")
    public ResponseEntity<?> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok("Card deleted");
    }

    @PostMapping("/{id}/request-block")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Запросить блокировку карты", description = "Пользователь отправляет запрос на блокировку своей карты")
    public ResponseEntity<CardDTO> requestBlock(@PathVariable Long id) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return ResponseEntity.ok(cardService.requestBlock(id, username));
    }
}