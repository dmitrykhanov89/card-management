package bankcards.service;

import bankcards.dto.TransferDTO;

import java.math.BigDecimal;
import java.util.List;

public interface TransferService {

    TransferDTO makeTransfer(Long fromCardId, Long toCardId, BigDecimal amount);
    List<TransferDTO> getTransfersByCardId(Long cardId);
}