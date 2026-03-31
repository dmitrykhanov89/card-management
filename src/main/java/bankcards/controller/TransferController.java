package bankcards.controller;

import bankcards.dto.TransferDTO;
import bankcards.dto.TransferCreateDTO;
import bankcards.entity.Card;
import bankcards.service.CardService;
import bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<TransferDTO> makeTransfer(@RequestBody TransferCreateDTO dto) {
        Card fromCard = cardService.findById(dto.getFromCardId())
                .orElseThrow(() -> new RuntimeException("FromCard not found"));
        Card toCard = cardService.findById(dto.getToCardId())
                .orElseThrow(() -> new RuntimeException("ToCard not found"));

        return ResponseEntity.ok(transferService.makeTransferDTO(fromCard, toCard, dto.getAmount()));
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<TransferDTO>> getCardTransfers(@PathVariable Long cardId) {
        Card card = cardService.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return ResponseEntity.ok(transferService.getTransfersByCardDTO(card));
    }
}