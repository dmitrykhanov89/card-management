package bankcards.controller;

import bankcards.dto.CardDTO;
import bankcards.dto.CardCreateDTO;
import bankcards.entity.CardStatus;
import bankcards.entity.User;
import bankcards.exception.ResourceNotFoundException;
import bankcards.service.CardService;
import bankcards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardControllerTest {

    private CardService cardService;
    private UserService userService;
    private CardController cardController;

    @BeforeEach
    void setUp() {
        cardService = mock(CardService.class);
        userService = mock(UserService.class);
        cardController = new CardController(cardService, userService);
    }

    @Test
    void createCard_ReturnsCardDTO() {
        CardCreateDTO dto = new CardCreateDTO("1234123412341234", LocalDate.of(2026,12,31), 1L, BigDecimal.valueOf(1000));
        CardDTO cardDTO = makeCardDTO(LocalDate.of(2026,12,31), CardStatus.ACTIVE, BigDecimal.valueOf(1000), false);
        when(cardService.createCard(1L, "1234123412341234", LocalDate.of(2026,12,31), BigDecimal.valueOf(1000)))
                .thenReturn(cardDTO);

        ResponseEntity<CardDTO> response = cardController.createCard(dto);

        assertNotNull(response);
        assert response.getBody() != null;
        assertEquals(1L, response.getBody().getId());
        assertEquals("**** **** **** 1234", response.getBody().getMaskedNumber());
    }

    @Test
    void getCard_ReturnsCardDTO() {
        CardDTO cardDTO = makeCardDTO(LocalDate.of(2026,12,31), CardStatus.ACTIVE, BigDecimal.valueOf(1000), false);
        when(cardService.getById(1L)).thenReturn(cardDTO);

        ResponseEntity<CardDTO> response = cardController.getCard(1L);

        assertNotNull(response);
        assert response.getBody() != null;
        assertEquals(1L, response.getBody().getId());
        verify(cardService, times(1)).getById(1L);
    }

    @Test
    void getUserCards_WhenUserExists_ReturnsPageOfCardDTO() {
        User user = new User();
        user.setId(1L);
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        CardDTO cardDTO = makeCardDTO(LocalDate.of(2026,12,31), CardStatus.ACTIVE, BigDecimal.valueOf(1000), false);
        Page<CardDTO> page = new PageImpl<>(List.of(cardDTO));
        when(cardService.getUserCards(user, PageRequest.of(0,10))).thenReturn(page);

        ResponseEntity<Page<CardDTO>> response = cardController.getUserCards(1L, 0, 10);

        assertNotNull(response);
        assert response.getBody() != null;
        assertEquals(1, response.getBody().getTotalElements());
        verify(cardService, times(1)).getUserCards(user, PageRequest.of(0,10));
    }

    @Test
    void getUserCards_WhenUserNotFound_ThrowsException() {
        when(userService.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cardController.getUserCards(1L, 0, 10));
    }

    @Test
    void updateStatus_ReturnsUpdatedCardDTO() {
        CardDTO cardDTO = makeCardDTO(LocalDate.of(2026,12,31), CardStatus.BLOCKED, BigDecimal.valueOf(1000), false);
        when(cardService.updateStatus(1L, CardStatus.BLOCKED)).thenReturn(cardDTO);

        ResponseEntity<CardDTO> response = cardController.updateStatus(1L, CardStatus.BLOCKED);

        assertNotNull(response);
        assert response.getBody() != null;
        assertEquals(CardStatus.BLOCKED, response.getBody().getStatus());
        verify(cardService, times(1)).updateStatus(1L, CardStatus.BLOCKED);
    }

    @Test
    void getBalance_ReturnsBalance() {
        when(cardService.getBalance(1L)).thenReturn(BigDecimal.valueOf(1000));

        ResponseEntity<?> response = cardController.getBalance(1L);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(1000), response.getBody());
        verify(cardService, times(1)).getBalance(1L);
    }

    @Test
    void deleteCard_VerifyServiceCalled() {
        ResponseEntity<?> response = cardController.deleteCard(1L);

        assertNotNull(response);
        assertEquals("Card deleted", response.getBody());
        verify(cardService, times(1)).deleteCard(1L);
    }

    @Test
    void requestBlock_ReturnsCardDTO() {
        CardDTO cardDTO = makeCardDTO(LocalDate.of(2026,12,31), CardStatus.ACTIVE, BigDecimal.valueOf(1000), true);
        when(cardService.requestBlock(1L, "john")).thenReturn(cardDTO);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("john");
        SecurityContextHolder.getContext().setAuthentication(auth);

        ResponseEntity<CardDTO> response = cardController.requestBlock(1L);

        assertNotNull(response);
        assert response.getBody() != null;
        assertTrue(response.getBody().isBlockRequested());
        verify(cardService, times(1)).requestBlock(1L, "john");
    }

    private CardDTO makeCardDTO(LocalDate expiry, CardStatus status, BigDecimal balance, boolean blockRequested) {
        return new CardDTO(1L, "**** **** **** " + "1234", expiry, status, balance, blockRequested);
    }
}