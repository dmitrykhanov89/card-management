package bankcards.service;

import bankcards.dto.TransferDTO;
import bankcards.entity.Card;
import bankcards.entity.Transfer;
import bankcards.exception.ResourceNotFoundException;
import bankcards.repository.CardRepository;
import bankcards.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;

    @Override
    @Transactional
    public TransferDTO makeTransfer(Long fromId, Long toId, BigDecimal amount) {

        Card from = getCard(fromId);
        Card to = getCard(toId);
        // ❗ Проверка владельца (ОБЯЗАТЕЛЬНО)
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        if (!from.getOwner().getUsername().equals(username) ||
                !to.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("You can transfer only between your cards");
        }
        // ❗ Проверка статуса
        if (from.getStatus() != bankcards.entity.CardStatus.ACTIVE ||
                to.getStatus() != bankcards.entity.CardStatus.ACTIVE) {
            throw new RuntimeException("Cards must be ACTIVE");
        }
        // ❗ Проверка баланса
        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        // 💰 Перевод
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        cardRepository.save(from);
        cardRepository.save(to);
        Transfer transfer = new Transfer();
        transfer.setFromCard(from);
        transfer.setToCard(to);
        transfer.setAmount(amount);
        transfer.setCreatedAt(LocalDateTime.now());
        return TransferDTO.fromEntity(transferRepository.save(transfer));
    }

    @Override
    public List<TransferDTO> getTransfersByCardId(Long cardId) {
        Card card = getCard(cardId);
        return transferRepository.findByFromCardOrToCard(card, card)
                .stream()
                .map(TransferDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private Card getCard(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
    }
}