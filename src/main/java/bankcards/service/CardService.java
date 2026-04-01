package bankcards.service;

import bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import bankcards.dto.CardDTO;
import bankcards.entity.User;
import java.math.BigDecimal;

public interface CardService {

    CardDTO createCard(Long ownerId, String cardNumber, java.time.LocalDate expirationDate, BigDecimal balance);
    CardDTO updateStatus(Long id, CardStatus status);
    CardDTO getById(Long id);
    Page<CardDTO> getUserCards(User user, Pageable pageable);
    BigDecimal getBalance(Long id);
    void deleteCard(Long id);
}