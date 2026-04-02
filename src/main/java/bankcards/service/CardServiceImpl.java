package bankcards.service;

import bankcards.dto.CardDTO;
import bankcards.entity.Card;
import bankcards.entity.CardStatus;
import bankcards.entity.User;
import bankcards.exception.BusinessException;
import bankcards.exception.ResourceNotFoundException;
import bankcards.repository.CardRepository;
import bankcards.repository.UserRepository;
import bankcards.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

/**
 * <p>
 * Реализация сервиса {@link CardService}, предоставляющая функциональность для управления банковскими картами.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Создание новой карты с шифрованием номера</li>
 *     <li>Обновление статуса карты (активация, блокировка, истёк срок)</li>
 *     <li>Получение баланса и информации о карте</li>
 *     <li>Удаление карт</li>
 *     <li>Запрос блокировки карты пользователем</li>
 *     <li>Поддержка пагинации при получении списка карт пользователя</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Override
    public CardDTO createCard(Long ownerId, String cardNumber, java.time.LocalDate expirationDate, BigDecimal balance) {
        log.info("Creating card for user ID: {}", ownerId);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.warn("User with ID: {} not found", ownerId);
                    return new ResourceNotFoundException("Пользователь не найден");
                });
        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setExpirationDate(expirationDate);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(balance);
        card.setOwner(owner);
        CardDTO result = CardDTO.fromEntity(cardRepository.save(card));
        log.info("Card created successfully with ID: {} for user: {}", result.getId(), owner.getUsername());
        return result;
    }

    @Override
    public CardDTO updateStatus(Long id, CardStatus status) {
        log.info("Updating card ID: {} status to {}", id, status);
        Card card = getCardOrThrow(id);
        CardStatus oldStatus = card.getStatus();
        card.setStatus(status);
        CardDTO result = CardDTO.fromEntity(cardRepository.save(card));
        log.info("Card ID: {} status changed from {} to {}", id, oldStatus, status);
        return result;
    }

    @Override
    public CardDTO getById(Long id) {
        log.debug("Fetching card by ID: {}", id);
        Card card = getCardOrThrow(id);
        securityService.validateCardAccess(card);
        return CardDTO.fromEntity(card);
    }

    @Override
    public Page<CardDTO> getUserCards(User user, CardStatus status, Pageable pageable) {
        log.debug("Fetching cards for user: {} with status: {}", user.getUsername(), status);
        Page<Card> cards;
        if (status != null) {
            cards = cardRepository.findByOwnerAndStatusAndDeletedFalse(user, status, pageable);
        } else {
            cards = cardRepository.findByOwnerAndDeletedFalse(user, pageable);
        }
        return cards.map(CardDTO::fromEntity);
    }

    @Override
    public BigDecimal getBalance(Long id) {
        log.debug("Fetching balance for card ID: {}", id);
        Card card = getCardOrThrow(id);
        securityService.validateCardAccess(card);
        return card.getBalance();
    }

    @Override
    public void deleteCard(Long id) {
        log.info("Soft deleting card ID: {}", id);
        Card card = getCardOrThrow(id);
        card.setDeleted(true);
        cardRepository.save(card);
        log.info("Card ID: {} marked as deleted", id);
    }

    @Override
    public CardDTO requestBlock(Long cardId, String username) {
        log.info("Block request for card ID: {} by user: {}", cardId, username);
        Card card = getCardOrThrow(cardId);
        if (!card.getOwner().getUsername().equals(username)) {
            log.warn("Block request denied for card ID: {} — user {} is not the owner", cardId, username);
            throw new AccessDeniedException("Вы можете запросить блокировку только для своей карты.");
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            log.warn("Block request denied for card ID: {} — card status: {}", cardId, card.getStatus());
            throw new BusinessException("Для блокировки можно запросить только АКТИВНЫЕ карты.");
        }
        card.setBlockRequested(true);
        CardDTO result = CardDTO.fromEntity(cardRepository.save(card));
        log.info("Block request for card ID: {} created successfully", cardId);
        return result;
    }

    private Card getCardOrThrow(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Card with ID: {} not found", id);
                    return new ResourceNotFoundException("Карта не найдена");
                });
    }
}