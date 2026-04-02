package bankcards.repository;

import bankcards.entity.Transfer;
import bankcards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <p>
 * Репозиторий для работы с сущностью перевода {@link Transfer}.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Стандартные CRUD-операции (с помощью JpaRepository)</li>
 *     <li>Получение всех переводов по карте-отправителю или карте-получателю</li>
 * </ul>
 */
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    /**
     * <p>Находит все переводы, где указанная карта является отправителем или получателем.</p>
     *
     * @param fromCard карта-отправитель
     * @param toCard карта-получатель
     * @return список переводов, связанных с указанной картой
     */
    List<Transfer> findByFromCardOrToCard(Card fromCard, Card toCard);
}