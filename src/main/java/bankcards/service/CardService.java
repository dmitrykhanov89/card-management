package bankcards.service;

import bankcards.dto.CardDTO;
import bankcards.entity.Card;
import bankcards.entity.User;
import bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CardService {

    Card createCard(Card card);
    CardDTO createCardDTO(Card card);

    Card updateCardStatus(Long id, CardStatus status);
    CardDTO updateCardStatusDTO(Long id, CardStatus status);

    Optional<Card> findById(Long id);
    CardDTO findByIdDTO(Long id);

    Page<Card> findCardsByUser(User owner, Pageable pageable);
    Page<CardDTO> findCardsByUserDTO(User owner, Pageable pageable);

    BigDecimal getBalance(Long id);

    void deleteCard(Long id);


    // Удалить
    Card updateBalance(Long id, BigDecimal amount);
    CardDTO updateBalanceDTO(Long id, BigDecimal amount);
}