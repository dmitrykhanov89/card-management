package bankcards.controller;

import bankcards.dto.LoginRequest;
import bankcards.dto.LoginResponse;
import bankcards.dto.RegisterRequest;
import bankcards.entity.Role;
import bankcards.entity.User;
import bankcards.exception.BusinessException;
import bankcards.security.JwtUtils;
import bankcards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private PasswordEncoder passwordEncoder;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtils = mock(JwtUtils.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authController = new AuthController(userService, authenticationManager, jwtUtils, passwordEncoder);
    }

    @Test
    void registerUser_WhenUsernameAvailable_RegistersUser() {
        RegisterRequest request = makeRegisterRequest("john", "pass");
        mockUserServiceForRegistration("john", "pass", "ROLE_USER");

        ResponseEntity<String> response = authController.registerUser(request);

        assertEquals("User registered", response.getBody());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void registerUser_WhenUsernameTaken_ThrowsBusinessException() {
        RegisterRequest request = makeRegisterRequest("john", "pass");
        when(userService.existsByUsername("john")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authController.registerUser(request));
    }

    @Test
    void login_WhenCalled_ReturnsToken() {
        LoginRequest request = makeLoginRequest();
        when(jwtUtils.generateToken("john")).thenReturn("token");

        ResponseEntity<LoginResponse> response = authController.login(request);

        assert response.getBody() != null;
        assertEquals("token", response.getBody().getToken());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void registerAdmin_WhenUsernameAvailable_RegistersAdmin() {
        RegisterRequest request = makeRegisterRequest("admin", "adminpass");
        mockUserServiceForRegistration("admin", "adminpass", "ROLE_ADMIN");

        ResponseEntity<String> response = authController.registerAdmin(request);

        assertEquals("Admin registered", response.getBody());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void registerAdmin_WhenUsernameTaken_ThrowsRuntimeException() {
        RegisterRequest request = makeRegisterRequest("admin", "adminpass");
        when(userService.existsByUsername("admin")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authController.registerAdmin(request));
    }

    private RegisterRequest makeRegisterRequest(String username, String password) {
        RegisterRequest r = new RegisterRequest();
        r.setUsername(username);
        r.setPassword(password);
        return r;
    }

    private LoginRequest makeLoginRequest() {
        LoginRequest r = new LoginRequest();
        r.setUsername("john");
        r.setPassword("pass");
        return r;
    }

    private void mockUserServiceForRegistration(String username, String password, String roleName) {
        when(userService.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encoded" + password);
        Role role = new Role();
        role.setName(roleName);
        when(userService.getRoleByName(roleName)).thenReturn(role);
    }
}