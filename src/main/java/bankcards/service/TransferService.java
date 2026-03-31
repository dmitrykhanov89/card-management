package bankcards.service;

import bankcards.dto.TransferDTO;
import bankcards.entity.Transfer;
import bankcards.entity.Card;

import java.math.BigDecimal;
import java.util.List;

public interface TransferService {

    Transfer makeTransfer(Card fromCard, Card toCard, BigDecimal amount);
    TransferDTO makeTransferDTO(Card fromCard, Card toCard, BigDecimal amount);

    List<Transfer> getTransfersByCard(Card card);
    List<TransferDTO> getTransfersByCardDTO(Card card);
}