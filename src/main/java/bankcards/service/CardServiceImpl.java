package bankcards.service;

import bankcards.dto.CardDTO;
import bankcards.entity.Card;
import bankcards.entity.CardStatus;
import bankcards.entity.User;
import bankcards.repository.CardRepository;
import bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Override
    public CardDTO createCard(Long ownerId, String cardNumber, java.time.LocalDate expirationDate, BigDecimal balance) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("User not found"));
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
        return CardDTO.fromEntity(getCardOrThrow(id));
    }

    @Override
    public Page<CardDTO> getUserCards(User user, Pageable pageable) {
        return cardRepository.findByOwner(user, pageable)
                .map(CardDTO::fromEntity);
    }

    @Override
    public BigDecimal getBalance(Long id) {
        return getCardOrThrow(id).getBalance();
    }

    @Override
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new RuntimeException("Card not found");
        }
        cardRepository.deleteById(id);
    }

    private Card getCardOrThrow(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }
}