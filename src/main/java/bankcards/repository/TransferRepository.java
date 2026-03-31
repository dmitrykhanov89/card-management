package bankcards.repository;

import bankcards.entity.Transfer;
import bankcards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    // Все переводы, где участвует карта
    List<Transfer> findByFromCardOrToCard(Card fromCard, Card toCard);
}