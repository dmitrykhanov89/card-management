package bankcards.service;

import bankcards.dto.TransferDTO;
import bankcards.entity.Card;
import bankcards.entity.CardStatus;
import bankcards.entity.Transfer;
import bankcards.entity.User;
import bankcards.exception.BusinessException;
import bankcards.exception.ResourceNotFoundException;
import bankcards.repository.CardRepository;
import bankcards.repository.TransferRepository;
import bankcards.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceImplTest {

    private TransferRepository transferRepository;
    private CardRepository cardRepository;
    private SecurityService securityService;
    private TransferServiceImpl transferService;

    @BeforeEach
    void setUp() {
        transferRepository = mock(TransferRepository.class);
        cardRepository = mock(CardRepository.class);
        securityService = mock(SecurityService.class);
        transferService = new TransferServiceImpl(transferRepository, cardRepository, securityService);

        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("john", ""));
    }

    private User makeUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private Card makeCard(Long id, User owner, CardStatus status, BigDecimal balance) {
        Card card = new Card();
        card.setId(id);
        card.setOwner(owner);
        card.setStatus(status);
        card.setBalance(balance);
        card.setCardNumber("1111222233334444");
        return card;
    }

    private Transfer makeTransfer(Card from, Card to, BigDecimal amount) {
        Transfer t = new Transfer();
        t.setFromCard(from);
        t.setToCard(to);
        t.setAmount(amount);
        t.setCreatedAt(LocalDateTime.now());
        return t;
    }

    @Test
    void makeTransfer_WhenAllValid_PerformsTransfer() {
        User john = makeUser(1L, "john");
        Card from = makeCard(1L, john, CardStatus.ACTIVE, BigDecimal.valueOf(1000));
        Card to = makeCard(2L, john, CardStatus.ACTIVE, BigDecimal.valueOf(500));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(i -> i.getArgument(0));

        transferService.makeTransfer(1L, 2L, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(800), from.getBalance());
        assertEquals(BigDecimal.valueOf(700), to.getBalance());

        ArgumentCaptor<Transfer> captor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepository).save(captor.capture());
        assertEquals(BigDecimal.valueOf(200), captor.getValue().getAmount());
    }

    @Test
    void makeTransfer_WhenFromCardNotOwnedByUser_ThrowsBusinessException() {
        User alice = makeUser(1L, "alice");
        User john = makeUser(2L, "john");
        Card from = makeCard(1L, alice, CardStatus.ACTIVE, BigDecimal.valueOf(1000));
        Card to = makeCard(2L, john, CardStatus.ACTIVE, BigDecimal.valueOf(500));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(BusinessException.class, () -> transferService.makeTransfer(1L, 2L, BigDecimal.valueOf(100)));
    }

    @Test
    void makeTransfer_WhenToCardNotOwnedByUser_ThrowsBusinessException() {
        User john = makeUser(1L, "john");
        User alice = makeUser(2L, "alice");
        Card from = makeCard(1L, john, CardStatus.ACTIVE, BigDecimal.valueOf(1000));
        Card to = makeCard(2L, alice, CardStatus.ACTIVE, BigDecimal.valueOf(500));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(BusinessException.class, () -> transferService.makeTransfer(1L, 2L, BigDecimal.valueOf(100)));
    }

    @Test
    void makeTransfer_WhenInsufficientBalance_ThrowsBusinessException() {
        User john = makeUser(1L, "john");
        Card from = makeCard(1L, john, CardStatus.ACTIVE, BigDecimal.valueOf(100));
        Card to = makeCard(2L, john, CardStatus.ACTIVE, BigDecimal.valueOf(500));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(BusinessException.class, () -> transferService.makeTransfer(1L, 2L, BigDecimal.valueOf(200)));
    }

    @Test
    void makeTransfer_WhenCardNotActive_ThrowsBusinessException() {
        User john = makeUser(1L, "john");
        Card from = makeCard(1L, john, CardStatus.BLOCKED, BigDecimal.valueOf(1000));
        Card to = makeCard(2L, john, CardStatus.ACTIVE, BigDecimal.valueOf(500));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(BusinessException.class, () -> transferService.makeTransfer(1L, 2L, BigDecimal.valueOf(100)));
    }

    @Test
    void getTransfersByCardId_WhenCalled_ReturnsListOfTransferDTO() {
        User john = makeUser(1L, "john");
        Card card = makeCard(1L, john, CardStatus.ACTIVE, BigDecimal.valueOf(1000));
        card.setCardNumber("1111222233334444");

        Card anotherCard = makeCard(2L, john, CardStatus.ACTIVE, BigDecimal.valueOf(500));
        anotherCard.setCardNumber("5555666677778888");

        Transfer transfer = makeTransfer(card, anotherCard, BigDecimal.valueOf(100));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        doNothing().when(securityService).validateCardAccess(card);
        when(transferRepository.findByFromCardOrToCard(any(Card.class), any(Card.class)))
                .thenReturn(List.of(transfer));

        List<TransferDTO> transfers = transferService.getTransfersByCardId(1L);

        assertNotNull(transfers);
        assertEquals(1, transfers.size());
        assertEquals("**** **** **** 4444", transfers.getFirst().getFromCardMasked());
        assertEquals("**** **** **** 8888", transfers.getFirst().getToCardMasked());

        verify(securityService).validateCardAccess(card);
        verify(transferRepository).findByFromCardOrToCard(any(Card.class), any(Card.class));
    }

    @Test
    void getTransfersByCardId_WhenCardNotFound_ThrowsResourceNotFoundException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transferService.getTransfersByCardId(1L));
    }
}