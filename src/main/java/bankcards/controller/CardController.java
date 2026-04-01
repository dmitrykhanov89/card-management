package bankcards.controller;

import bankcards.dto.CardDTO;
import bankcards.dto.CardCreateDTO;
import bankcards.entity.Card;
import bankcards.entity.CardStatus;
import bankcards.entity.User;
import bankcards.service.CardService;
import bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    // ---------------- Создание карты — только ADMIN ----------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> createCard(@RequestBody CardCreateDTO dto) {
        User owner = userService.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setExpirationDate(dto.getExpirationDate());
        card.setOwner(owner);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);

        return ResponseEntity.ok(cardService.createCardDTO(card));
    }

    // ---------------- Получение карты по ID — ADMIN только ----------------
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.findByIdDTO(id));
    }

    // ---------------- Получение карт конкретного пользователя с пагинацией — ADMIN только ----------------
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardDTO>> getUserCards(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User owner = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(cardService.findCardsByUserDTO(owner, PageRequest.of(page, size)));
    }

    // ---------------- Обновление статуса карты — только ADMIN ----------------
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> updateCardStatus(@PathVariable Long id,
                                                    @RequestParam CardStatus status) {
        return ResponseEntity.ok(cardService.updateCardStatusDTO(id, status));
    }

    // ---------------- Получение баланса карты — USER и ADMIN ----------------
    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getBalance(id));
    }

    // ---------------- Установка баланса карты — только ADMIN ----------------
    @PatchMapping("/{id}/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDTO> setBalance(@PathVariable Long id,
                                              @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(cardService.updateBalanceDTO(id, amount));
    }

    // ---------------- Получение своих карт — USER и ADMIN ----------------
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Page<CardDTO>> getMyCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Получаем текущего пользователя
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<CardDTO> cards = cardService.findCardsByUserDTO(owner, PageRequest.of(page, size));
        return ResponseEntity.ok(cards);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCard(@PathVariable Long id) {
        Card card = cardService.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        cardService.deleteCard(id); // нужно реализовать метод в CardService
        return ResponseEntity.ok("Card deleted successfully");
    }
}