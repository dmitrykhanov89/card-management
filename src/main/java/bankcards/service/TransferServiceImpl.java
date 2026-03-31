package bankcards.service;

import bankcards.dto.TransferDTO;
import bankcards.entity.Transfer;
import bankcards.entity.Card;
import bankcards.repository.TransferRepository;
import bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;

    @Override
    @Transactional
    public Transfer makeTransfer(Card fromCard, Card toCard, BigDecimal amount) {
        if (fromCard.getStatus() != bankcards.entity.CardStatus.ACTIVE ||
                toCard.getStatus() != bankcards.entity.CardStatus.ACTIVE) {
            throw new RuntimeException("Both cards must be ACTIVE for transfer");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        Transfer transfer = new Transfer();
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);
        transfer.setAmount(amount);
        transfer.setCreatedAt(LocalDateTime.now());

        return transferRepository.save(transfer);
    }

    @Override
    public TransferDTO makeTransferDTO(Card fromCard, Card toCard, BigDecimal amount) {
        return new TransferDTO(makeTransfer(fromCard, toCard, amount));
    }

    @Override
    public List<Transfer> getTransfersByCard(Card card) {
        return transferRepository.findByFromCardOrToCard(card, card);
    }

    @Override
    public List<TransferDTO> getTransfersByCardDTO(Card card) {
        return getTransfersByCard(card)
                .stream()
                .map(TransferDTO::new)
                .collect(Collectors.toList());
    }
}