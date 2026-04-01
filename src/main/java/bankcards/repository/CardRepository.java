package bankcards.repository;

import bankcards.entity.Card;
import bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByOwner(User owner, Pageable pageable);
}