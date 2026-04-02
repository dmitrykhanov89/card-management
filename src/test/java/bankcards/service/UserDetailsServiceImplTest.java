package bankcards.service;

import bankcards.entity.Role;
import bankcards.entity.User;
import bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    private Role makeRole() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return role;
    }

    private User makeUser() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("pass");
        Set<Role> roleSet;
        roleSet = Set.of(makeRole());
        user.setRoles(roleSet);
        return user;
    }

    @Test
    void loadUserByUsername_WhenUserExists_ReturnsUserDetails() {
        User user = makeUser();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("john");

        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("pass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_USER")));
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ThrowsUsernameNotFoundException() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("john"));
    }
}