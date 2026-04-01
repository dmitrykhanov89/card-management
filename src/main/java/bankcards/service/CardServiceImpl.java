package bankcards.service;

import bankcards.dto.CardDTO;
import bankcards.entity.Card;
import bankcards.entity.CardStatus;
import bankcards.entity.User;
import bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public Card createCard(Card card) {
        card.setStatus(CardStatus.ACTIVE);
        return cardRepository.save(card);
    }

    @Override
    public CardDTO createCardDTO(Card card) {
        return new CardDTO(createCard(card));
    }

    @Override
    public Card updateCardStatus(Long id, CardStatus status) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id));
        card.setStatus(status);
        return cardRepository.save(card);
    }

    @Override
    public CardDTO updateCardStatusDTO(Long id, CardStatus status) {
        return new CardDTO(updateCardStatus(id, status));
    }

    @Override
    public Optional<Card> findById(Long id) {
        return cardRepository.findById(id);
    }

    @Override
    public CardDTO findByIdDTO(Long id) {
        return new CardDTO(findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id)));
    }

    @Override
    public Page<Card> findCardsByUser(User owner, Pageable pageable) {
        return cardRepository.findByOwner(owner, pageable);
    }

    @Override
    public Page<CardDTO> findCardsByUserDTO(User owner, Pageable pageable) {
        return findCardsByUser(owner, pageable).map(CardDTO::new);
    }

    @Override
    public BigDecimal getBalance(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + id))
                .getBalance();
    }

    @Override
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new RuntimeException("Card not found with id: " + id);
        }
        cardRepository.deleteById(id);
    }

    // Удалить
    @Override
    public Card updateBalance(Long id, BigDecimal amount) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setBalance(amount);
        return cardRepository.save(card);
    }

    @Override
    public CardDTO updateBalanceDTO(Long id, BigDecimal amount) {
        return new CardDTO(updateBalance(id, amount));
    }
}