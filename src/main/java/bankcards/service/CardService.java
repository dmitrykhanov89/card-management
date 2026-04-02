package bankcards.service;

import bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import bankcards.dto.CardDTO;
import bankcards.entity.User;
import java.math.BigDecimal;

/**
 * <p>
 * Сервис для управления банковскими картами.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Создание новой карты для пользователя</li>
 *     <li>Изменение статуса карты (Активна, Заблокирована, Истёк срок)</li>
 *     <li>Получение информации о карте и балансе</li>
 *     <li>Удаление карты</li>
 *     <li>Получение списка карт пользователя с поддержкой пагинации</li>
 *     <li>Запрос блокировки карты пользователем</li>
 * </ul>
 */
public interface CardService {

    /**
     * Создаёт новую карту для указанного пользователя.
     *
     * @param ownerId идентификатор владельца карты
     * @param cardNumber номер карты (будет зашифрован и маскирован)
     * @param expirationDate дата окончания действия карты
     * @param balance начальный баланс карты
     * @return DTO созданной карты
     */
    CardDTO createCard(Long ownerId, String cardNumber, java.time.LocalDate expirationDate, BigDecimal balance);

    /**
     * Обновляет статус карты.
     *
     * @param id идентификатор карты
     * @param status новый статус карты
     * @return DTO обновлённой карты
     */
    CardDTO updateStatus(Long id, CardStatus status);

    /**
     * Возвращает карту по её идентификатору.
     *
     * @param id идентификатор карты
     * @return DTO карты
     */
    CardDTO getById(Long id);

    /**
     * Получает список карт пользователя с пагинацией.
     *
     * @param user пользователь
     * @param status фильтр по статусу (опционально)
     * @param pageable параметры пагинации
     * @return страница DTO карт пользователя
     */
    Page<CardDTO> getUserCards(User user, CardStatus status, Pageable pageable);

    /**
     * Получает текущий баланс карты.
     *
     * @param id идентификатор карты
     * @return баланс карты
     */
    BigDecimal getBalance(Long id);

    /**
     * Удаляет карту по идентификатору.
     *
     * @param id идентификатор карты
     */
    void deleteCard(Long id);

    /**
     * Запрашивает блокировку карты от имени пользователя.
     *
     * @param cardId идентификатор карты
     * @param username имя пользователя, который инициирует блокировку
     * @return DTO карты с обновлённым статусом (например, "Заблокирована")
     */
    CardDTO requestBlock(Long cardId, String username);
}