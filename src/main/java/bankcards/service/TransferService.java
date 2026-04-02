package bankcards.service;

import bankcards.dto.TransferDTO;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * Сервис для управления переводами между банковскими картами.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Выполнение перевода между двумя картами одного пользователя</li>
 *     <li>Получение истории переводов по карте</li>
 * </ul>
 */
public interface TransferService {

    /**
     * Выполняет перевод между двумя картами.
     *
     * @param fromCardId идентификатор карты списания
     * @param toCardId идентификатор карты зачисления
     * @param amount сумма перевода
     * @return DTO выполненного перевода
     */
    TransferDTO makeTransfer(Long fromCardId, Long toCardId, BigDecimal amount);

    /**
     * Получает список всех переводов, связанных с указанной картой.
     *
     * @param cardId идентификатор карты
     * @return список DTO переводов
     */
    List<TransferDTO> getTransfersByCardId(Long cardId);
}