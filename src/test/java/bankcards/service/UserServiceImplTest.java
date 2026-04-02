package bankcards.service;

import bankcards.dto.UserDTO;
import bankcards.entity.Role;
import bankcards.entity.User;
import bankcards.exception.BusinessException;
import bankcards.exception.ResourceNotFoundException;
import bankcards.repository.RoleRepository;
import bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        userService = new UserServiceImpl(userRepository, roleRepository);
    }

    private User makeUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private Role makeRole() {
        Role role = new Role();
        role.setName("USER");
        return role;
    }

    @Test
    void createUser_WhenCalled_SavesUser() {
        User user = makeUser(null, "test");
        userService.createUser(user);
        verify(userRepository).save(user);
    }

    @Test
    void findByUsername_WhenUserExists_ReturnsUser() {
        User user = makeUser(1L, "john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("john");

        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
    }

    @Test
    void existsByUsername_WhenUserExists_ReturnsTrue() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);
        assertTrue(userService.existsByUsername("alice"));
    }

    @Test
    void findById_WhenUserExists_ReturnsUser() {
        User user = makeUser(1L, "bob");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getRoleByName_WhenRoleExists_ReturnsRole() {
        Role role = makeRole();
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

        Role result = userService.getRoleByName("USER");

        assertEquals("USER", result.getName());
    }

    @Test
    void getRoleByName_WhenRoleDoesNotExist_ThrowsBusinessException() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userService.getRoleByName("ADMIN"));
    }

    @Test
    void findAllDTO_WhenCalled_ReturnsListOfUserDTO() {
        User user = makeUser(1L, "bob");
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDTO> result = userService.findAllDTO();

        assertEquals(1, result.size());
        assertEquals("bob", result.getFirst().getUsername());
    }

    @Test
    void findByUsernameDTO_WhenUserExists_ReturnsUserDTO() {
        User user = makeUser(1L, "carol");
        when(userRepository.findByUsername("carol")).thenReturn(Optional.of(user));

        UserDTO dto = userService.findByUsernameDTO("carol");

        assertEquals("carol", dto.getUsername());
    }

    @Test
    void findByUsernameDTO_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findByUsername("dave")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.findByUsernameDTO("dave"));
    }
}