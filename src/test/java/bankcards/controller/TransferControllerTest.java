package bankcards.controller;

import bankcards.dto.TransferDTO;
import bankcards.dto.TransferCreateDTO;
import bankcards.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferControllerTest {

    private TransferService transferService;
    private TransferController transferController;

    @BeforeEach
    void setUp() {
        transferService = mock(TransferService.class);
        transferController = new TransferController(transferService);
    }

    @Test
    void makeTransfer_ReturnsTransferDTO() {
        TransferCreateDTO dto = new TransferCreateDTO(1L, 2L, BigDecimal.valueOf(200));
        TransferDTO transferDTO = makeTransferDTO(BigDecimal.valueOf(200));
        when(transferService.makeTransfer(1L, 2L, BigDecimal.valueOf(200))).thenReturn(transferDTO);

        ResponseEntity<TransferDTO> response = transferController.makeTransfer(dto);

        assertNotNull(response);
        assert response.getBody() != null;
        assertEquals(transferDTO.getId(), response.getBody().getId());
        assertEquals(transferDTO.getAmount(), response.getBody().getAmount());
        verify(transferService, times(1)).makeTransfer(1L, 2L, BigDecimal.valueOf(200));
    }

    @Test
    void getCardTransfers_ReturnsListOfTransferDTO() {
        Long cardId = 1L;
        TransferDTO transferDTO = makeTransferDTO(BigDecimal.valueOf(200));
        when(transferService.getTransfersByCardId(cardId)).thenReturn(List.of(transferDTO));

        ResponseEntity<List<TransferDTO>> response = transferController.getCardTransfers(cardId);

        assertNotNull(response);
        assert response.getBody() != null;
        assertEquals(1, response.getBody().size());
        assertEquals(transferDTO.getId(), response.getBody().getFirst().getId());
        verify(transferService, times(1)).getTransfersByCardId(cardId);
    }

    private TransferDTO makeTransferDTO(BigDecimal amount) {
        return new TransferDTO(
                1L,
                "**** **** **** " + "1234",
                "**** **** **** " + "5678",
                amount,
                LocalDateTime.now()
        );
    }
}