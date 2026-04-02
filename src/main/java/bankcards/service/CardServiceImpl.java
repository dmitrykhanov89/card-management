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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Override
    public CardDTO createCard(Long ownerId, String cardNumber, java.time.LocalDate expirationDate, BigDecimal balance) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setExpirationDate(expirationDate);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(balance);
        card.setOwner(owner);
        return CardDTO.fromEntity(cardRepository.save(card));
    }

    @Override
    public CardDTO updateStatus(Long id, CardStatus status) {
        Card card = getCardOrThrow(id);
        card.setStatus(status);
        return CardDTO.fromEntity(cardRepository.save(card));
    }

    @Override
    public CardDTO getById(Long id) {
        Card card = getCardOrThrow(id);
        securityService.validateCardAccess(card);
        return CardDTO.fromEntity(card);
    }

    @Override
    public Page<CardDTO> getUserCards(User user, Pageable pageable) {
        return cardRepository.findByOwner(user, pageable)
                .map(CardDTO::fromEntity);
    }

    @Override
    public BigDecimal getBalance(Long id) {
        Card card = getCardOrThrow(id);
        securityService.validateCardAccess(card);
        return card.getBalance();
    }

    @Override
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Card not found");
        }
        cardRepository.deleteById(id);
    }

    @Override
    public CardDTO requestBlock(Long cardId, String username) {
        Card card = getCardOrThrow(cardId);
        if (!card.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You can request block only for your own card");
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("Only ACTIVE cards can be requested for block");
        }
        card.setBlockRequested(true);
        return CardDTO.fromEntity(cardRepository.save(card));
    }

    private Card getCardOrThrow(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
    }
}