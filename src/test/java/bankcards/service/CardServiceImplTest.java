package bankcards.service;

import bankcards.dto.CardDTO;
import bankcards.entity.Card;
import bankcards.entity.CardStatus;
import bankcards.entity.User;
import bankcards.exception.BusinessException;
import bankcards.exception.ResourceNotFoundException;
import bankcards.repository.CardRepository;
import bankcards.repository.UserRepository;
import bankcards.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    private CardRepository cardRepository;
    private UserRepository userRepository;
    private SecurityService securityService;
    private CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        userRepository = mock(UserRepository.class);
        securityService = mock(SecurityService.class);
        cardService = new CardServiceImpl(cardRepository, userRepository, securityService);
    }

    private User makeUser(String username) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        return user;
    }

    private Card makeCard(User owner, CardStatus status, BigDecimal balance) {
        Card card = new Card();
        card.setOwner(owner);
        card.setStatus(status);
        card.setBlockRequested(false);
        card.setBalance(balance);
        card.setCardNumber("1111222233334444");
        card.setExpirationDate(LocalDate.now().plusYears(1));
        return card;
    }

    @Test
    void createCard_WhenUserExists_ReturnsCardDTO() {
        User user = makeUser("john");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

        cardService.createCard(1L, "1111222233334444", LocalDate.now().plusYears(1), BigDecimal.valueOf(1000));

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(captor.capture());
        Card savedCard = captor.getValue();

        assertEquals("1111222233334444", savedCard.getCardNumber());
        assertEquals(user, savedCard.getOwner());
        assertEquals(CardStatus.ACTIVE, savedCard.getStatus());
    }

    @Test
    void createCard_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> cardService.createCard(1L, "1234", LocalDate.now(), BigDecimal.ZERO));
    }

    @Test
    void updateStatus_WhenCardExists_UpdatesStatus() {
        Card card = makeCard(null, CardStatus.ACTIVE, BigDecimal.ZERO);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        cardService.updateStatus(1L, CardStatus.BLOCKED);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }

    @Test
    void getById_WhenCardExistsAndAccessAllowed_ReturnsCardDTO() {
        Card card = makeCard(null, CardStatus.ACTIVE, BigDecimal.ZERO);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        doNothing().when(securityService).validateCardAccess(card);

        CardDTO dto = cardService.getById(1L);

        assertNotNull(dto);
        verify(securityService).validateCardAccess(card);
    }

    @Test
    void getById_WhenCardDoesNotExist_ThrowsResourceNotFoundException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cardService.getById(1L));
    }

    @Test
    void getBalance_WhenCardExistsAndAccessAllowed_ReturnsBalance() {
        Card card = makeCard(null, CardStatus.ACTIVE, BigDecimal.valueOf(500));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        doNothing().when(securityService).validateCardAccess(card);

        BigDecimal balance = cardService.getBalance(1L);

        assertEquals(BigDecimal.valueOf(500), balance);
    }

    @Test
    void deleteCard_WhenCardExists_DeletesCard() {
        when(cardRepository.existsById(1L)).thenReturn(true);

        cardService.deleteCard(1L);

        verify(cardRepository).deleteById(1L);
    }

    @Test
    void deleteCard_WhenCardDoesNotExist_ThrowsResourceNotFoundException() {
        when(cardRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> cardService.deleteCard(1L));
    }

    @Test
    void requestBlock_WhenCardIsActiveAndOwnedByUser_SetsBlockRequested() {
        User user = makeUser("john");
        Card card = makeCard(user, CardStatus.ACTIVE, BigDecimal.ZERO);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        cardService.requestBlock(1L, "john");

        assertTrue(card.isBlockRequested());
    }

    @Test
    void requestBlock_WhenCardNotOwnedByUser_ThrowsAccessDeniedException() {
        User user = makeUser("alice");
        Card card = makeCard(user, CardStatus.ACTIVE, BigDecimal.ZERO);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(AccessDeniedException.class, () -> cardService.requestBlock(1L, "bob"));
    }

    @Test
    void requestBlock_WhenCardNotActive_ThrowsBusinessException() {
        User user = makeUser("john");
        Card card = makeCard(user, CardStatus.BLOCKED, BigDecimal.ZERO);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(BusinessException.class, () -> cardService.requestBlock(1L, "john"));
    }

    @Test
    void getUserCards_WhenCalled_ReturnsPageOfCardDTO() {
        User user = makeUser("john");
        Card card = makeCard(user, CardStatus.ACTIVE, BigDecimal.ZERO);
        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findByOwner(eq(user), any(Pageable.class))).thenReturn(page);

        Page<CardDTO> result = cardService.getUserCards(user, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }
}