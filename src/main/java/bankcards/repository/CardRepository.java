package bankcards.repository;

import bankcards.entity.Card;
import bankcards.entity.CardStatus;
import bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p>
 * Репозиторий для работы с сущностью банковской карты {@link Card}.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Стандартные CRUD-операции (с помощью JpaRepository)</li>
 *     <li>Получение карт по владельцу с поддержкой постраничного вывода</li>
 *     <li>Фильтрация карт по статусу</li>
 * </ul>
 */
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * <p>Находит не удалённые карты, принадлежащие указанному пользователю, с поддержкой постраничного вывода.</p>
     *
     * @param owner владелец карт
     * @param pageable объект для постраничной навигации
     * @return страница карт пользователя
     */
    Page<Card> findByOwnerAndDeletedFalse(User owner, Pageable pageable);

    /**
     * <p>Находит не удалённые карты по владельцу и статусу с поддержкой постраничного вывода.</p>
     *
     * @param owner владелец карт
     * @param status статус карты
     * @param pageable объект для постраничной навигации
     * @return страница карт пользователя с указанным статусом
     */
    Page<Card> findByOwnerAndStatusAndDeletedFalse(User owner, CardStatus status, Pageable pageable);
}