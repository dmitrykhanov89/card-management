package bankcards.repository;

import bankcards.entity.Card;
import bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    // Все карты пользователя с пагинацией
    Page<Card> findByOwner(User owner, Pageable pageable);

    // Поиск карты по номеру (для переводов)
    Optional<Card> findByCardNumber(String cardNumber);
}