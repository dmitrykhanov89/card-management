package bankcards.security;

import bankcards.entity.Card;
import bankcards.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SecurityServiceTest {

    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService();
    }

    private User makeUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    private Card makeCard(User owner) {
        Card card = new Card();
        card.setOwner(owner);
        return card;
    }

    private void authenticate(String username, String... roles) {
        List<SimpleGrantedAuthority> authorities = roles == null ? List.of() :
                java.util.Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .toList();
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(username, "password", authorities));
    }

    @Test
    void validateCardAccess_WhenUserIsOwner_DoesNotThrow() {
        User owner = makeUser("john");
        Card card = makeCard(owner);

        authenticate("john");

        assertDoesNotThrow(() -> securityService.validateCardAccess(card));
    }

    @Test
    void validateCardAccess_WhenUserIsAdmin_DoesNotThrow() {
        User owner = makeUser("alice");
        Card card = makeCard(owner);

        authenticate("john", "ROLE_ADMIN");

        assertDoesNotThrow(() -> securityService.validateCardAccess(card));
    }

    @Test
    void validateCardAccess_WhenUserIsNotOwnerNorAdmin_ThrowsAccessDeniedException() {
        User owner = makeUser("alice");
        Card card = makeCard(owner);

        authenticate("john");

        assertThrows(AccessDeniedException.class, () -> securityService.validateCardAccess(card));
    }
}