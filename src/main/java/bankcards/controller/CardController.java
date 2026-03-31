package bankcards.controller;

import bankcards.dto.CardDTO;
import bankcards.dto.CardCreateDTO;
import bankcards.entity.Card;
import bankcards.entity.User;
import bankcards.entity.CardStatus;
import bankcards.service.CardService;
import bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    // Создание карты
    @PostMapping
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

    // Получение карты по ID
    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.findByIdDTO(id));
    }

    // Получение карт пользователя с пагинацией
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CardDTO>> getUserCards(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User owner = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(cardService.findCardsByUserDTO(owner, PageRequest.of(page, size)));
    }

    // Обновление статуса карты
    @PatchMapping("/{id}/status")
    public ResponseEntity<CardDTO> updateCardStatus(@PathVariable Long id, @RequestParam CardStatus status) {
        return ResponseEntity.ok(cardService.updateCardStatusDTO(id, status));
    }

    // Получение баланса карты
    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getBalance(id));
    }

    // CardController.java
    @PatchMapping("/{id}/balance")
    public ResponseEntity<CardDTO> setBalance(@PathVariable Long id,
                                              @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(cardService.updateBalanceDTO(id, amount));
    }
}