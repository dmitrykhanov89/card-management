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

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Совершить перевод", description = "Позволяет пользователю перевести деньги между своими картами")
    public ResponseEntity<TransferDTO> makeTransfer(@RequestBody TransferCreateDTO dto) {
        return ResponseEntity.ok(transferService.makeTransfer(dto.getFromCardId(), dto.getToCardId(), dto.getAmount()));
    }

    @GetMapping("/card/{cardId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Получить переводы по карте", description = "Возвращает список всех переводов для указанной карты")
    public ResponseEntity<List<TransferDTO>> getCardTransfers(@PathVariable Long cardId) {
        return ResponseEntity.ok(transferService.getTransfersByCardId(cardId));
    }
}