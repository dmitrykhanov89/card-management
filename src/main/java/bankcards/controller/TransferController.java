package bankcards.controller;

import bankcards.dto.TransferDTO;
import bankcards.dto.TransferCreateDTO;
import bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * Контроллер для управления переводами между банковскими картами.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Совершение переводов между своими картами (USER и ADMIN)</li>
 *     <li>Просмотр истории переводов для конкретной карты</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    /**
     * Совершает перевод между картами текущего пользователя.
     *
     * @param dto объект {@link TransferCreateDTO} с данными перевода:
     *            идентификаторы карт-отправителя и получателя, а также сумма перевода
     * @return {@link ResponseEntity} с {@link TransferDTO} выполненного перевода
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Совершить перевод", description = "Позволяет пользователю перевести деньги между своими картами")
    public ResponseEntity<TransferDTO> makeTransfer(@RequestBody TransferCreateDTO dto) {
        return ResponseEntity.ok(transferService.makeTransfer(dto.getFromCardId(), dto.getToCardId(), dto.getAmount()));
    }

    /**
     * Получает список всех переводов для указанной карты.
     *
     * @param cardId идентификатор карты
     * @return {@link ResponseEntity} с {@link List} {@link TransferDTO} переводов по карте
     */
    @GetMapping("/card/{cardId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Получить переводы по карте", description = "Возвращает список всех переводов для указанной карты")
    public ResponseEntity<List<TransferDTO>> getCardTransfers(@PathVariable Long cardId) {
        return ResponseEntity.ok(transferService.getTransfersByCardId(cardId));
    }
}