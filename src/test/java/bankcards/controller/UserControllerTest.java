package bankcards.controller;

import bankcards.dto.UserDTO;
import bankcards.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserServiceImpl userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserServiceImpl.class);
        userController = new UserController(userService);
    }

    @Test
    void getAllUsers_ReturnsListOfUserDTO() {
        List<UserDTO> users = List.of(
                makeUserDTO(1L, "john"),
                makeUserDTO(2L, "alice")
        );
        when(userService.findAllDTO()).thenReturn(users);

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertNotNull(response);
        assert response.getBody() != null;
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).findAllDTO();
    }

    @Test
    void getUser_WhenUserExists_ReturnsUserDTO() {
        UserDTO userDTO = makeUserDTO(1L, "john");
        when(userService.findByUsernameDTO("john")).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUser("john");

        assertNotNull(response);
        assert response.getBody() != null;
        assertEquals("john", response.getBody().getUsername());
        assertEquals(Set.of("ROLE_USER"), response.getBody().getRoles());
        verify(userService, times(1)).findByUsernameDTO("john");
    }

    @Test
    void getUser_WhenUserNotFound_ThrowsException() {
        when(userService.findByUsernameDTO("unknown")).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> userController.getUser("unknown"));
        verify(userService, times(1)).findByUsernameDTO("unknown");
    }

    private UserDTO makeUserDTO(Long id, String username) {
        return new UserDTO(id, username, Set.of("ROLE_USER"));
    }
}